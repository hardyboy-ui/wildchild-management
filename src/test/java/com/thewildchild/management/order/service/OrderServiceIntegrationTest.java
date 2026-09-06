package com.thewildchild.management.order.service;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.order.service.impl.OrderServiceImpl;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private MenuItemAddOnRepository menuItemAddOnRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        diningSessionRepository.deleteAll();
        cafeTableRepository.deleteAll();
        menuItemAddOnRepository.deleteAll();
        addOnRepository.deleteAll();
        menuItemRepository.deleteAll();
        menuCategoryRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() {

        DiningSession session = createDiningSession();

        var response =
                orderService.createOrder(session.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getOrderNumber()).isNotBlank();


        Order savedOrder =
                orderRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(savedOrder.getDiningSession().getId())
                .isEqualTo(session.getId());

        assertThat(savedOrder.getStatus())
                .isEqualTo(OrderStatus.OPEN);

        assertThat(savedOrder.getBillingStatus())
                .isEqualTo(OrderBillingStatus.UNBILLED);
    }

    @Test
    void shouldGetOrderById() {

        DiningSession session = createDiningSession();

        Order order = createOrder(session);

        var response =
                orderService.getOrderById(order.getId());

        assertThat(response.getId())
                .isEqualTo(order.getId());

        assertThat(response.getOrderNumber())
                .isEqualTo(order.getOrderNumber());

    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        assertThatThrownBy(() ->
                orderService.getOrderById(orderId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void shouldAddOrderItemWithoutAddOns() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Cold Coffee",
                new BigDecimal("150.00")
        );

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItem.getId());
        request.setQuantity(2);

        var response =
                orderService.addOrderItem(
                        order.getId(),
                        request
                );

        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo(order.getId());

        Order savedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(savedOrder.getItems())
                .hasSize(1);

        OrderItem item =
                savedOrder.getItems().get(0);

        assertThat(item.getMenuItem().getId())
                .isEqualTo(menuItem.getId());

        assertThat(item.getQuantity())
                .isEqualTo(2);

        assertThat(item.getUnitPrice())
                .isEqualByComparingTo("150.00");

        assertThat(item.getKotSentQuantity())
                .isZero();

        assertThat(item.getAddOns())
                .isEmpty();
    }

    @Test
    void shouldUpdateQuantityWhenMatchingOrderItemAlreadyExists() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Cold Coffee",
                new BigDecimal("150.00")
        );

        AddOrderItemRequest firstRequest =
                new AddOrderItemRequest();

        firstRequest.setMenuItemId(menuItem.getId());
        firstRequest.setQuantity(2);

        orderService.addOrderItem(
                order.getId(),
                firstRequest
        );

        AddOrderItemRequest secondRequest =
                new AddOrderItemRequest();

        secondRequest.setMenuItemId(menuItem.getId());
        secondRequest.setQuantity(5);

        orderService.addOrderItem(
                order.getId(),
                secondRequest
        );

        Order savedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(savedOrder.getItems())
                .hasSize(1);

        assertThat(savedOrder.getItems().get(0).getQuantity())
                .isEqualTo(5);
    }

    @Test
    void shouldAddOrderItemWithValidAddOn() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Cold Coffee",
                new BigDecimal("150.00")
        );

        AddOn addOn = createAddOn(
                "Extra Ice Cream",
                new BigDecimal("50.00")
        );

        createMenuItemAddOn(menuItem, addOn);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItem.getId());
        request.setQuantity(2);

        var addOnRequest =
                new com.thewildchild.management.order.dto.request
                        .AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOn.getId());

        request.setAddOns(
                java.util.List.of(addOnRequest)
        );

        orderService.addOrderItem(
                order.getId(),
                request
        );

        Order savedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(savedOrder.getItems())
                .hasSize(1);

        OrderItem item =
                savedOrder.getItems().get(0);

        assertThat(item.getAddOns())
                .hasSize(1);

        assertThat(
                item.getAddOns().get(0).getAddOn().getId()
        )
                .isEqualTo(addOn.getId());

        assertThat(
                item.getAddOns().get(0).getUnitPrice()
        )
                .isEqualByComparingTo("50.00");
    }

    @Test
    void shouldRejectInactiveMenuItem() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Unavailable Item",
                new BigDecimal("100.00")
        );

        menuItem.setActive(false);
        menuItemRepository.save(menuItem);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItem.getId());
        request.setQuantity(1);

        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        order.getId(),
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Menu item is not active");
    }

    @Test
    void shouldRejectNonExistingMenuItem() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(1);

        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        order.getId(),
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu item not found");
    }

    @Test
    void shouldRejectInactiveAddOn() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Cold Coffee",
                new BigDecimal("150.00")
        );

        AddOn addOn = createAddOn(
                "Inactive AddOn",
                new BigDecimal("50.00")
        );

        addOn.setActive(false);
        addOnRepository.save(addOn);

        createMenuItemAddOn(menuItem, addOn);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItem.getId());
        request.setQuantity(1);

        var addOnRequest =
                new com.thewildchild.management.order.dto.request
                        .AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOn.getId());

        request.setAddOns(
                java.util.List.of(addOnRequest)
        );

        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        order.getId(),
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Add-on is not active");
    }

    @Test
    void shouldRejectAddOnNotAvailableForMenuItem() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        MenuItem menuItem = createMenuItem(
                "Cold Coffee",
                new BigDecimal("150.00")
        );

        AddOn addOn = createAddOn(
                "Extra Ice Cream",
                new BigDecimal("50.00")
        );

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItem.getId());
        request.setQuantity(1);

        var addOnRequest =
                new com.thewildchild.management.order.dto.request
                        .AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOn.getId());

        request.setAddOns(
                java.util.List.of(addOnRequest)
        );

        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        order.getId(),
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(
                        "is not available for menu item"
                );
    }

    @Test
    void shouldCloseOrder() {

        DiningSession session = createDiningSession();
        Order order = createOrder(session);

        order.setBillingStatus(OrderBillingStatus.PAID);

        var response =
                orderService.closeOrder(order.getId());


        Order savedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(savedOrder.getStatus())
                .isEqualTo(OrderStatus.CLOSED);
    }

    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------

    private CafeTable createTable() {

        CafeTable table = new CafeTable();

        table.setTableNumber(
                "T-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        table.setCapacity(4);
        table.setDisplayOrder(1);
        table.setActive(true);

        return cafeTableRepository.save(table);
    }

    private DiningSession createDiningSession() {

        CafeTable table = createTable();

        DiningSession session =
                new DiningSession();

        session.setSessionNumber(
                "DS-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        session.setTable(table);
        session.setStatus(DiningSessionStatus.OPEN);
        session.setOpenedAt(
                java.time.LocalDateTime.now()
        );

        return diningSessionRepository.save(session);
    }

    private Order createOrder(
            DiningSession session
    ) {

        Order order = new Order();

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        order.setDiningSession(session);
        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(
                OrderBillingStatus.UNBILLED
        );

        return orderRepository.save(order);
    }

    private MenuCategory createCategory() {

        MenuCategory category =
                new MenuCategory();

        category.setName(
                "Category-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        category.setDescription("Test category");
        category.setActive(true);
        category.setDisplayOrder(1);

        return menuCategoryRepository.save(category);
    }

    private MenuItem createMenuItem(
            String name,
            BigDecimal price
    ) {

        MenuItem menuItem = new MenuItem();

        menuItem.setName(name);
        menuItem.setDescription("Test menu item");
        menuItem.setPrice(price);
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setActive(true);
        menuItem.setCategory(createCategory());

        return menuItemRepository.save(menuItem);
    }

    private AddOn createAddOn(
            String name,
            BigDecimal price
    ) {

        AddOn addOn = new AddOn();

        addOn.setName(name);
        addOn.setDescription("Test add-on");
        addOn.setPrice(price);
        addOn.setActive(true);

        return addOnRepository.save(addOn);
    }

    private void createMenuItemAddOn(
            MenuItem menuItem,
            AddOn addOn
    ) {

        com.thewildchild.management.menu.entity.MenuItemAddOn
                menuItemAddOn =
                new com.thewildchild.management.menu.entity.MenuItemAddOn();

        menuItemAddOn.setMenuItem(menuItem);
        menuItemAddOn.setAddOn(addOn);

        menuItemAddOnRepository.save(menuItemAddOn);
    }
}