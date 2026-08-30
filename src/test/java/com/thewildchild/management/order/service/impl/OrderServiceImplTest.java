package com.thewildchild.management.order.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.dto.request.AddOrderItemAddOnRequest;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.dto.response.OrderResponse;
import com.thewildchild.management.order.entity.*;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.order.service.mapper.OrderMapper;
import com.thewildchild.management.order.service.validator.OrderValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private AddOnRepository addOnRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private MenuItemAddOnRepository menuItemAddOnRepository;

    @InjectMocks
    private OrderServiceImpl orderService;


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Test
    void shouldCreateOrderSuccessfully() {

        // Arrange
        UUID diningSessionId = UUID.randomUUID();

        DiningSession diningSession =
                new DiningSession();

        Order savedOrder =
                new Order();

        OrderResponse response =
                new OrderResponse();

        when(diningSessionRepository.findById(diningSessionId))
                .thenReturn(Optional.of(diningSession));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderMapper.toResponse(savedOrder))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.createOrder(diningSessionId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(diningSessionRepository)
                .findById(diningSessionId);

        verify(orderRepository)
                .save(any(Order.class));

        verify(orderMapper)
                .toResponse(savedOrder);
    }


    @Test
    void shouldThrowExceptionWhenDiningSessionDoesNotExist() {

        // Arrange
        UUID diningSessionId = UUID.randomUUID();

        when(diningSessionRepository.findById(diningSessionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.createOrder(diningSessionId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Dining session not found with id: "
                                + diningSessionId
                );

        verify(diningSessionRepository)
                .findById(diningSessionId);

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Test
    void shouldGetOrderByIdSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order =
                new Order();

        OrderResponse response =
                new OrderResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.getOrderById(orderId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(orderRepository)
                .findById(orderId);

        verify(orderMapper)
                .toResponse(order);
    }


    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.getOrderById(orderId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Order not found with id: " + orderId
                );

        verify(orderRepository)
                .findById(orderId);

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }


    // =========================================================
    // ADD ORDER ITEM
    // =========================================================

    @Test
    void shouldAddNewOrderItemSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setQuantity(2);

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);
        menuItem.setPrice(new BigDecimal("250.00"));

        Order savedOrder =
                new Order();

        OrderResponse response =
                new OrderResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(orderRepository.save(order))
                .thenReturn(savedOrder);

        when(orderMapper.toResponse(savedOrder))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.addOrderItem(
                        orderId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(order.getItems())
                .hasSize(1);

        OrderItem orderItem =
                order.getItems().get(0);

        assertThat(orderItem.getMenuItem())
                .isSameAs(menuItem);

        assertThat(orderItem.getQuantity())
                .isEqualTo(2);

        assertThat(orderItem.getKotSentQuantity())
                .isZero();

        assertThat(orderItem.getUnitPrice())
                .isEqualByComparingTo("250.00");

        // Verify
        verify(orderRepository)
                .findById(orderId);

        verify(orderValidator)
                .validateRequest(request);

        verify(menuItemRepository)
                .findById(menuItemId);

        verify(orderRepository)
                .save(order);

        verify(orderMapper)
                .toResponse(savedOrder);
    }


    @Test
    void shouldThrowExceptionWhenOrderDoesNotExistWhileAddingItem() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Order not found with id: " + orderId
                );

        verify(orderRepository)
                .findById(orderId);

        verify(orderValidator, never())
                .validateRequest(any());

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void shouldThrowExceptionWhenMenuItemDoesNotExist() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + menuItemId
                );

        verify(orderValidator)
                .validateRequest(request);

        verify(menuItemRepository)
                .findById(menuItemId);

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void shouldThrowExceptionWhenMenuItemIsInactive() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(false);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Menu item is not active");

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    // =========================================================
    // ADD ORDER ITEM WITH ADD-ONS
    // =========================================================

    @Test
    void shouldAddOrderItemWithValidAddOnsSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        AddOrderItemAddOnRequest addOnRequest =
                new AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOnId);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setQuantity(1);
        request.setAddOns(List.of(addOnRequest));

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);
        menuItem.setPrice(new BigDecimal("200.00"));

        AddOn addOn =
                new AddOn();

        addOn.setId(addOnId);
        addOn.setName("Extra Cheese");
        addOn.setActive(true);
        addOn.setPrice(new BigDecimal("50.00"));

        Order savedOrder =
                new Order();

        OrderResponse response =
                new OrderResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findAllById(List.of(addOnId)))
                .thenReturn(List.of(addOn));

        when(menuItemAddOnRepository
                .existsByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                ))
                .thenReturn(true);

        when(orderRepository.save(order))
                .thenReturn(savedOrder);

        when(orderMapper.toResponse(savedOrder))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.addOrderItem(
                        orderId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(order.getItems())
                .hasSize(1);

        OrderItem orderItem =
                order.getItems().get(0);

        assertThat(orderItem.getAddOns())
                .hasSize(1);

        assertThat(orderItem.getAddOns().get(0).getAddOn())
                .isSameAs(addOn);

        assertThat(orderItem.getAddOns().get(0).getUnitPrice())
                .isEqualByComparingTo("50.00");

        // Verify
        verify(addOnRepository)
                .findAllById(List.of(addOnId));

        verify(menuItemAddOnRepository)
                .existsByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void shouldThrowExceptionWhenAddOnDoesNotExist() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        AddOrderItemAddOnRequest addOnRequest =
                new AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOnId);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setQuantity(1);
        request.setAddOns(List.of(addOnRequest));

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findAllById(List.of(addOnId)))
                .thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "One or more add-ons were not found"
                );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void shouldThrowExceptionWhenAddOnIsInactive() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        AddOrderItemAddOnRequest addOnRequest =
                new AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOnId);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setAddOns(List.of(addOnRequest));

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);

        AddOn addOn =
                new AddOn();

        addOn.setId(addOnId);
        addOn.setName("Extra Cheese");
        addOn.setActive(false);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findAllById(List.of(addOnId)))
                .thenReturn(List.of(addOn));

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Add-on is not active: Extra Cheese"
                );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void shouldThrowExceptionWhenAddOnIsNotAvailableForMenuItem() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        AddOrderItemAddOnRequest addOnRequest =
                new AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(addOnId);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setAddOns(List.of(addOnRequest));

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);

        AddOn addOn =
                new AddOn();

        addOn.setId(addOnId);
        addOn.setName("Extra Cheese");
        addOn.setActive(true);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findAllById(List.of(addOnId)))
                .thenReturn(List.of(addOn));

        when(menuItemAddOnRepository
                .existsByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                ))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.addOrderItem(
                        orderId,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Add-on 'Extra Cheese' is not available for menu item"
                );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    // =========================================================
    // UPDATE EXISTING ORDER ITEM
    // =========================================================

    @Test
    void shouldUpdateQuantityWhenMatchingOrderItemAlreadyExists() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setQuantity(5);

        Order order =
                new Order();

        order.setItems(new ArrayList<>());

        MenuItem menuItem =
                new MenuItem();

        menuItem.setId(menuItemId);
        menuItem.setActive(true);
        menuItem.setPrice(new BigDecimal("200.00"));

        OrderItem existingItem =
                new OrderItem();

        existingItem.setOrder(order);
        existingItem.setMenuItem(menuItem);
        existingItem.setQuantity(2);
        existingItem.setAddOns(new ArrayList<>());

        order.getItems().add(existingItem);

        OrderResponse response =
                new OrderResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.addOrderItem(
                        orderId,
                        request
                );

        // Assert
        assertThat(existingItem.getQuantity())
                .isEqualTo(5);

        assertThat(order.getItems())
                .hasSize(1);

        assertThat(result)
                .isSameAs(response);

        verify(orderRepository)
                .save(order);
    }


    // =========================================================
    // CLOSE ORDER
    // =========================================================

    @Test
    void shouldCloseOrderSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order =
                new Order();

        OrderResponse response =
                new OrderResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        // Act
        OrderResponse result =
                orderService.closeOrder(orderId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.CLOSED);

        verify(orderRepository)
                .findById(orderId);

        verify(orderValidator)
                .validateCanBeClosed(order);

        verify(orderRepository)
                .save(order);

        verify(orderMapper)
                .toResponse(order);
    }


    @Test
    void shouldThrowExceptionWhenClosingNonExistingOrder() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.closeOrder(orderId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Order not found with id: " + orderId
                );

        verify(orderValidator, never())
                .validateCanBeClosed(any(Order.class));

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void shouldNotCloseOrderWhenValidationFails() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order =
                new Order();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        doThrow(new BusinessException(
                "Order cannot be closed"
        ))
                .when(orderValidator)
                .validateCanBeClosed(order);

        // Act & Assert
        assertThatThrownBy(() ->
                orderService.closeOrder(orderId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Order cannot be closed"
                );

        // The service must not change the status
        assertThat(order.getStatus())
                .isNotEqualTo(OrderStatus.CLOSED);

        verify(orderValidator)
                .validateCanBeClosed(order);

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}
