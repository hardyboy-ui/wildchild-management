package com.thewildchild.management.invoice.service;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.entity.DiscountType;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceBillingType;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
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
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvoiceServiceIntegrationTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    private DiningSession diningSession;

    private Order order;

    private MenuItem menuItem;


    @BeforeEach
    void setUp() {

        MenuCategory category = new MenuCategory();

        category.setName("Test Category");
        category.setDescription("Test category for integration tests");
        category.setActive(true);
        category.setDisplayOrder(1);

        category = menuCategoryRepository.saveAndFlush(category);

        /*
         * ---------------------------------------------------------
         * MENU ITEM
         * ---------------------------------------------------------
         */
        menuItem = new MenuItem();

        menuItem.setName("Test Burger");
        menuItem.setPrice(new BigDecimal("100.00"));
        menuItem.setActive(true);
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setCategory(category);

        menuItem = menuItemRepository.saveAndFlush(menuItem);


        /*
         * ---------------------------------------------------------
         * TABLE
         * ---------------------------------------------------------
         */

        CafeTable table = new CafeTable();

        table.setTableNumber("T-TEST");
        table.setCapacity(4);
        table.setActive(true);
        table.setDisplayOrder(1);

        table = cafeTableRepository.saveAndFlush(table);


        /*
         * ---------------------------------------------------------
         * DINING SESSION
         * ---------------------------------------------------------
         */

        diningSession = new DiningSession();

        diningSession.setSessionNumber("DS-TEST");
        diningSession.setTable(table);
        diningSession.setStatus(DiningSessionStatus.OPEN);
        diningSession.setOpenedAt(LocalDateTime.now());

        diningSession = diningSessionRepository.saveAndFlush(diningSession);


        /*
         * ---------------------------------------------------------
         * ORDER
         * ---------------------------------------------------------
         */

        order = new Order();

        order.setOrderNumber("ORD-TEST");
        order.setDiningSession(diningSession);
        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);



        /*
         * ---------------------------------------------------------
         * ORDER ITEM
         * ---------------------------------------------------------
         */

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("100.00"));
        orderItem.setKotSentQuantity(2);

        order.getItems().add(orderItem);


        /*
         * ---------------------------------------------------------
         * SAVE ORDER
         * ---------------------------------------------------------
         */

        order = orderRepository.saveAndFlush(order);
    }


    // ============================================================
    // ORDER INVOICE
    // ============================================================

    @Test
    void shouldGenerateOrderInvoice() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);


        InvoiceResponse response =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        /*
         * Invoice generated
         */

        assertThat(response)
                .isNotNull();

        assertThat(response.getId())
                .isNotNull();

        assertThat(response.getInvoiceNumber())
                .startsWith("INV-");

        assertThat(response.getBillingType())
                .isEqualTo(InvoiceBillingType.ORDER);

        assertThat(response.getStatus())
                .isEqualTo(InvoiceStatus.GENERATED);


        /*
         * Subtotal
         *
         * 2 × 100 = 200
         */

        assertThat(response.getSubtotal())
                .isEqualByComparingTo("200.00");


        /*
         * Discount
         */

        assertThat(response.getDiscountAmount())
                .isEqualByComparingTo("0.00");


        /*
         * Tax
         */

        assertThat(response.getTaxAmount())
                .isEqualByComparingTo("0.00");


        /*
         * Grand total
         */

        assertThat(response.getGrandTotal())
                .isEqualByComparingTo("200.00");


        /*
         * Order must now be BILLED
         */

        Order updatedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(updatedOrder.getBillingStatus())
                .isEqualTo(OrderBillingStatus.BILLED);


        /*
         * Verify actual database invoice
         */

        Invoice savedInvoice =
                invoiceRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(savedInvoice.getInvoiceNumber())
                .isEqualTo(response.getInvoiceNumber());

        assertThat(savedInvoice.getBillingType())
                .isEqualTo(InvoiceBillingType.ORDER);

        assertThat(savedInvoice.getStatus())
                .isEqualTo(InvoiceStatus.GENERATED);

        assertThat(savedInvoice.getInvoiceOrders())
                .hasSize(1);

        assertThat(
                savedInvoice.getInvoiceOrders()
                        .get(0)
                        .getOrder()
                        .getId()
        )
                .isEqualTo(order.getId());
    }


    // ============================================================
    // DISCOUNT
    // ============================================================

    @Test
    void shouldGenerateInvoiceWithPercentageDiscount() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(
                DiscountType.PERCENTAGE
        );

        request.setDiscountValue(
                new BigDecimal("10.00")
        );

        request.setTaxRate(
                BigDecimal.ZERO
        );


        InvoiceResponse response =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        /*
         * Subtotal = 200
         *
         * 10% discount = 20
         */

        assertThat(response.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(response.getDiscountAmount())
                .isEqualByComparingTo("20.00");


        /*
         * Taxable amount = 180
         */

        assertThat(response.getGrandTotal())
                .isEqualByComparingTo("180.00");
    }


    // ============================================================
    // DISCOUNT + TAX
    // ============================================================

    @Test
    void shouldGenerateInvoiceWithDiscountAndTax() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(
                DiscountType.PERCENTAGE
        );

        request.setDiscountValue(
                new BigDecimal("10.00")
        );

        request.setTaxRate(
                new BigDecimal("5.00")
        );


        InvoiceResponse response =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        /*
         * Subtotal = 200
         *
         * Discount = 10% = 20
         *
         * Taxable = 180
         *
         * Tax = 5% of 180 = 9
         *
         * Grand total = 189
         */

        assertThat(response.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(response.getDiscountAmount())
                .isEqualByComparingTo("20.00");

        assertThat(response.getTaxAmount())
                .isEqualByComparingTo("9.00");

        assertThat(response.getGrandTotal())
                .isEqualByComparingTo("189.00");
    }


    // ============================================================
    // FIXED DISCOUNT
    // ============================================================

    @Test
    void shouldGenerateInvoiceWithFixedDiscount() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(
                DiscountType.FIXED
        );

        request.setDiscountValue(
                new BigDecimal("30.00")
        );

        request.setTaxRate(
                BigDecimal.ZERO
        );


        InvoiceResponse response =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        assertThat(response.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(response.getDiscountAmount())
                .isEqualByComparingTo("30.00");

        assertThat(response.getGrandTotal())
                .isEqualByComparingTo("170.00");
    }


    // ============================================================
    // DINING SESSION INVOICE
    // ============================================================

    @Test
    void shouldGenerateDiningSessionInvoice() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);
        request.setTaxRate(BigDecimal.ZERO);


        InvoiceResponse response =
                invoiceService.generateDiningSessionInvoice(
                        diningSession.getId(),
                        request
                );


        assertThat(response)
                .isNotNull();

        assertThat(response.getBillingType())
                .isEqualTo(
                        InvoiceBillingType.DINING_SESSION
                );

        assertThat(response.getStatus())
                .isEqualTo(
                        InvoiceStatus.GENERATED
                );

        assertThat(response.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(response.getGrandTotal())
                .isEqualByComparingTo("200.00");


        /*
         * Order should now be billed.
         */

        Order updatedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(updatedOrder.getBillingStatus())
                .isEqualTo(OrderBillingStatus.BILLED);


        /*
         * Invoice should contain the order.
         */

        Invoice invoice =
                invoiceRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(invoice.getInvoiceOrders())
                .hasSize(1);
    }


    // ============================================================
    // GET INVOICE
    // ============================================================

    @Test
    void shouldGetInvoiceById() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);


        InvoiceResponse generated =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        InvoiceResponse response =
                invoiceService.getInvoiceById(
                        generated.getId()
                );


        assertThat(response)
                .isNotNull();

        assertThat(response.getId())
                .isEqualTo(generated.getId());

        assertThat(response.getInvoiceNumber())
                .isEqualTo(
                        generated.getInvoiceNumber()
                );
    }


    // ============================================================
    // CANCEL INVOICE
    // ============================================================

    @Test
    void shouldCancelGeneratedInvoice() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);


        InvoiceResponse generated =
                invoiceService.generateOrderInvoice(
                        order.getId(),
                        request
                );


        /*
         * Order is now BILLED.
         */

        Order billedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(billedOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.BILLED
                );


        /*
         * Cancel invoice.
         */

        InvoiceResponse cancelled =
                invoiceService.cancelInvoice(
                        generated.getId()
                );


        assertThat(cancelled.getStatus())
                .isEqualTo(
                        InvoiceStatus.CANCELLED
                );


        /*
         * Order should become UNBILLED again.
         */

        Order revertedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(revertedOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.UNBILLED
                );


        /*
         * Verify database state.
         */

        Invoice invoice =
                invoiceRepository.findById(
                        generated.getId()
                ).orElseThrow();

        assertThat(invoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.CANCELLED
                );
    }


    // ============================================================
    // ORDER NOT FOUND
    // ============================================================

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);


        assertThatThrownBy(() ->
                invoiceService.generateOrderInvoice(
                        java.util.UUID.randomUUID(),
                        request
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                );
    }


    // ============================================================
    // INVOICE NOT FOUND
    // ============================================================

    @Test
    void shouldThrowExceptionWhenInvoiceDoesNotExist() {

        assertThatThrownBy(() ->
                invoiceService.getInvoiceById(
                        java.util.UUID.randomUUID()
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                );
    }


    // ============================================================
    // NO UNBILLED ORDERS
    // ============================================================

    @Test
    void shouldThrowExceptionWhenDiningSessionHasNoUnbilledOrders() {

        /*
         * Mark the existing order as already billed.
         */

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        orderRepository.save(order);


        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscountType(null);
        request.setDiscountValue(BigDecimal.ZERO);


        assertThatThrownBy(() ->
                invoiceService.generateDiningSessionInvoice(
                        diningSession.getId(),
                        request
                )
        )
                .isInstanceOf(
                        BusinessException.class
                );
    }
}