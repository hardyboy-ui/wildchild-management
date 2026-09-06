package com.thewildchild.management.order.repository;

import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceBillingType;
import com.thewildchild.management.invoice.entity.InvoiceOrder;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.invoice.repository.InvoiceOrderRepository;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class OrderItemRepositoryIntegrationTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceOrderRepository invoiceOrderRepository;

    @Test
    void shouldFindAllOrderItemsByOrderId() {

        // Arrange

        CafeTable table = new CafeTable();
        table.setTableNumber("T-TEST-01");
        table.setCapacity(4);
        table.setDisplayOrder(1);
        table.setActive(true);

        cafeTableRepository.save(table);


        DiningSession session = new DiningSession();
        session.setSessionNumber("SESSION-TEST-01");
        session.setTable(table);
        session.setStatus(DiningSessionStatus.OPEN);
        session.setOpenedAt(LocalDateTime.now());

        diningSessionRepository.save(session);


        MenuCategory category = new MenuCategory();
        category.setName("Test Category");
        category.setDescription("Test category");
        category.setDisplayOrder(1);
        category.setActive(true);

        menuCategoryRepository.save(category);


        MenuItem menuItem = new MenuItem();
        menuItem.setName("Test Burger");
        menuItem.setDescription("Test burger");
        menuItem.setPrice(new BigDecimal("250.00"));
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setActive(true);
        menuItem.setCategory(category);

        menuItemRepository.save(menuItem);


        Order order = new Order();
        order.setOrderNumber("ORDER-TEST-01");
        order.setDiningSession(session);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);
        order.setStatus(OrderStatus.OPEN);

        orderRepository.save(order);


        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("250.00"));
        orderItem.setKotSentQuantity(0);
        orderItem.setSpecialInstructions("No onions");

        orderItemRepository.save(orderItem);


        // Act

        var results =
                orderItemRepository.findAllByOrderId(order.getId());


        // Assert

        assertThat(results)
                .hasSize(1);

        assertThat(results.get(0).getId())
                .isEqualTo(orderItem.getId());

        assertThat(results.get(0).getQuantity())
                .isEqualTo(2);

        assertThat(results.get(0).getUnitPrice())
                .isEqualByComparingTo("250.00");

        assertThat(results.get(0).getMenuItem().getName())
                .isEqualTo("Test Burger");
    }

    @Test
    void shouldReturnTopSellingMenuItemsForPaidInvoices() {

        // Arrange

        CafeTable table = new CafeTable();
        table.setTableNumber("T-TEST-02");
        table.setCapacity(4);
        table.setDisplayOrder(2);
        table.setActive(true);

        cafeTableRepository.save(table);


        DiningSession session = new DiningSession();
        session.setSessionNumber("SESSION-TEST-02");
        session.setTable(table);
        session.setStatus(DiningSessionStatus.CLOSED);
        session.setOpenedAt(LocalDateTime.now().minusHours(2));
        session.setClosedAt(LocalDateTime.now());

        diningSessionRepository.save(session);


        MenuCategory category = new MenuCategory();
        category.setName("Test Category 2");
        category.setDescription("Test category");
        category.setDisplayOrder(2);
        category.setActive(true);

        menuCategoryRepository.save(category);


        MenuItem burger = new MenuItem();
        burger.setName("Test Burger 2");
        burger.setDescription("Test burger");
        burger.setPrice(new BigDecimal("250.00"));
        burger.setFoodType(FoodType.VEG);
        burger.setActive(true);
        burger.setCategory(category);

        menuItemRepository.save(burger);


        MenuItem pasta = new MenuItem();
        pasta.setName("Test Pasta 2");
        pasta.setDescription("Test pasta");
        pasta.setPrice(new BigDecimal("300.00"));
        pasta.setFoodType(FoodType.VEG);
        pasta.setActive(true);
        pasta.setCategory(category);

        menuItemRepository.save(pasta);


        Order order = new Order();
        order.setOrderNumber("ORDER-TEST-02");
        order.setDiningSession(session);
        order.setBillingStatus(OrderBillingStatus.PAID);
        order.setStatus(OrderStatus.CLOSED);

        orderRepository.save(order);


        OrderItem burgerItem = new OrderItem();
        burgerItem.setOrder(order);
        burgerItem.setMenuItem(burger);
        burgerItem.setQuantity(3);
        burgerItem.setUnitPrice(new BigDecimal("250.00"));
        burgerItem.setKotSentQuantity(3);

        orderItemRepository.save(burgerItem);


        OrderItem pastaItem = new OrderItem();
        pastaItem.setOrder(order);
        pastaItem.setMenuItem(pasta);
        pastaItem.setQuantity(2);
        pastaItem.setUnitPrice(new BigDecimal("300.00"));
        pastaItem.setKotSentQuantity(2);

        orderItemRepository.save(pastaItem);


        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-TEST-02");
        invoice.setBillingType(InvoiceBillingType.ORDER);
        invoice.setSubtotal(new BigDecimal("1350.00"));
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTaxRate(BigDecimal.ZERO);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setGrandTotal(new BigDecimal("1350.00"));
        invoice.setStatus(InvoiceStatus.PAID);

        invoiceRepository.save(invoice);


        InvoiceOrder invoiceOrder = new InvoiceOrder();
        invoiceOrder.setInvoice(invoice);
        invoiceOrder.setOrder(order);

        invoiceOrderRepository.save(invoiceOrder);

        orderItemRepository.flush();


        // Act

        LocalDateTime start =
                LocalDateTime.now().minusHours(1);

        LocalDateTime end =
                LocalDateTime.now().plusHours(1);

        var results =
                orderItemRepository.getTopSellingMenuItems(
                        start,
                        end
                );


        // Assert

        assertThat(results)
                .hasSize(2);

        assertThat(results.get(0).getMenuItemName())
                .isEqualTo("Test Burger 2");

        assertThat(results.get(0).getQuantitySold())
                .isEqualTo(3);

        assertThat(results.get(0).getRevenue())
                .isEqualByComparingTo("750.00");


        assertThat(results.get(1).getMenuItemName())
                .isEqualTo("Test Pasta 2");

        assertThat(results.get(1).getQuantitySold())
                .isEqualTo(2);

        assertThat(results.get(1).getRevenue())
                .isEqualByComparingTo("600.00");
    }



    @Test
    void shouldReturnCategorySalesForPaidInvoices() {

        // Arrange

        CafeTable table = new CafeTable();
        table.setTableNumber("T-TEST-03");
        table.setCapacity(4);
        table.setDisplayOrder(3);
        table.setActive(true);

        cafeTableRepository.save(table);


        DiningSession session = new DiningSession();
        session.setSessionNumber("SESSION-TEST-03");
        session.setTable(table);
        session.setStatus(DiningSessionStatus.CLOSED);
        session.setOpenedAt(LocalDateTime.now().minusHours(2));
        session.setClosedAt(LocalDateTime.now());

        diningSessionRepository.save(session);


        MenuCategory category = new MenuCategory();
        category.setName("Test Category 3");
        category.setDescription("Category sales test");
        category.setDisplayOrder(3);
        category.setActive(true);

        menuCategoryRepository.save(category);


        MenuItem burger = new MenuItem();
        burger.setName("Category Test Burger");
        burger.setPrice(new BigDecimal("250.00"));
        burger.setFoodType(FoodType.VEG);
        burger.setActive(true);
        burger.setCategory(category);

        menuItemRepository.save(burger);


        MenuItem sandwich = new MenuItem();
        sandwich.setName("Category Test Sandwich");
        sandwich.setPrice(new BigDecimal("200.00"));
        sandwich.setFoodType(FoodType.VEG);
        sandwich.setActive(true);
        sandwich.setCategory(category);

        menuItemRepository.save(sandwich);


        Order order = new Order();
        order.setOrderNumber("ORDER-TEST-03");
        order.setDiningSession(session);
        order.setBillingStatus(OrderBillingStatus.PAID);
        order.setStatus(OrderStatus.CLOSED);

        orderRepository.save(order);


        OrderItem burgerItem = new OrderItem();
        burgerItem.setOrder(order);
        burgerItem.setMenuItem(burger);
        burgerItem.setQuantity(3);
        burgerItem.setUnitPrice(new BigDecimal("250.00"));
        burgerItem.setKotSentQuantity(3);

        orderItemRepository.save(burgerItem);


        OrderItem sandwichItem = new OrderItem();
        sandwichItem.setOrder(order);
        sandwichItem.setMenuItem(sandwich);
        sandwichItem.setQuantity(2);
        sandwichItem.setUnitPrice(new BigDecimal("200.00"));
        sandwichItem.setKotSentQuantity(2);

        orderItemRepository.save(sandwichItem);


        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-TEST-03");
        invoice.setBillingType(InvoiceBillingType.ORDER);
        invoice.setSubtotal(new BigDecimal("1150.00"));
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTaxRate(BigDecimal.ZERO);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setGrandTotal(new BigDecimal("1150.00"));
        invoice.setStatus(InvoiceStatus.PAID);

        invoiceRepository.save(invoice);


        InvoiceOrder invoiceOrder = new InvoiceOrder();
        invoiceOrder.setInvoice(invoice);
        invoiceOrder.setOrder(order);

        invoiceOrderRepository.save(invoiceOrder);

        orderItemRepository.flush();


        // Act

        LocalDateTime start =
                LocalDateTime.now().minusHours(1);

        LocalDateTime end =
                LocalDateTime.now().plusHours(1);

        var results =
                orderItemRepository.getCategorySales(
                        start,
                        end
                );


        // Assert

        assertThat(results)
                .hasSize(1);

        assertThat(results.get(0).getCategoryName())
                .isEqualTo("Test Category 3");

        assertThat(results.get(0).getQuantitySold())
                .isEqualTo(5);

        assertThat(results.get(0).getRevenue())
                .isEqualByComparingTo("1150.00");
    }
}