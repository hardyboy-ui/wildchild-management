package com.thewildchild.management.order.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.order.dto.response.KotResponse;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderItemAddOn;
import com.thewildchild.management.order.repository.OrderRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KotServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private KotServiceImpl kotService;


    @Test
    void shouldGenerateKotSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID orderItemId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setItems(new ArrayList<>());

        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setName("Burger");

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setKotSentQuantity(0);
        orderItem.setSpecialInstructions("No onions");
        orderItem.setAddOns(new ArrayList<>());

        order.getItems().add(orderItem);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        KotResponse result =
                kotService.generateKot(orderId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderId())
                .isEqualTo(orderId);
        assertThat(result.getOrderNumber())
                .isEqualTo("ORD-12345678");

        assertThat(result.getItems())
                .hasSize(1);

        assertThat(result.getItems().get(0).getOrderItemId())
                .isEqualTo(orderItemId);

        assertThat(result.getItems().get(0).getMenuItemId())
                .isEqualTo(menuItemId);

        assertThat(result.getItems().get(0).getMenuItemName())
                .isEqualTo("Burger");

        assertThat(result.getItems().get(0).getQuantity())
                .isEqualTo(2);

        assertThat(result.getItems().get(0).getSpecialInstructions())
                .isEqualTo("No onions");

        // Verify state change
        assertThat(orderItem.getKotSentQuantity())
                .isEqualTo(2);

        // Verify
        verify(orderRepository)
                .findById(orderId);

        verify(orderRepository)
                .save(order);
    }


    @Test
    void shouldGenerateKotOnlyForPendingQuantity() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setItems(new ArrayList<>());

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID());
        menuItem.setName("Pizza");

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setMenuItem(menuItem);

        orderItem.setQuantity(5);
        orderItem.setKotSentQuantity(2);
        orderItem.setAddOns(new ArrayList<>());

        order.getItems().add(orderItem);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        KotResponse result =
                kotService.generateKot(orderId);

        // Assert
        assertThat(result.getItems())
                .hasSize(1);

        assertThat(result.getItems().get(0).getQuantity())
                .isEqualTo(3);

        assertThat(orderItem.getKotSentQuantity())
                .isEqualTo(5);

        verify(orderRepository)
                .save(order);
    }


    @Test
    void shouldIgnoreOrderItemsAlreadySentToKot() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setItems(new ArrayList<>());

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID());
        menuItem.setName("Coffee");

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setKotSentQuantity(2);
        orderItem.setAddOns(new ArrayList<>());

        order.getItems().add(orderItem);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() ->
                kotService.generateKot(orderId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "No new items available for KOT"
                );
    }


    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                kotService.generateKot(orderId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Order not found with id: " + orderId
                );

        verify(orderRepository)
                .findById(orderId);
    }


    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setItems(new ArrayList<>());

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() ->
                kotService.generateKot(orderId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "No new items available for KOT"
                );
    }


    @Test
    void shouldIncludeAddOnsInKot() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setItems(new ArrayList<>());

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID());
        menuItem.setName("Burger");

        AddOn addOn = new AddOn();
        addOn.setId(UUID.randomUUID());
        addOn.setName("Extra Cheese");

        OrderItemAddOn orderItemAddOn =
                new OrderItemAddOn();

        orderItemAddOn.setId(UUID.randomUUID());
        orderItemAddOn.setAddOn(addOn);
        orderItemAddOn.setUnitPrice(
                BigDecimal.valueOf(50)
        );

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(1);
        orderItem.setKotSentQuantity(0);

        orderItem.setAddOns(
                new ArrayList<>(
                        List.of(orderItemAddOn)
                )
        );

        order.getItems().add(orderItem);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        KotResponse result =
                kotService.generateKot(orderId);

        // Assert
        assertThat(result.getItems())
                .hasSize(1);

        assertThat(result.getItems().get(0).getAddOns())
                .hasSize(1);

        assertThat(
                result.getItems()
                        .get(0)
                        .getAddOns()
                        .get(0)
                        .getAddOnId()
        )
                .isEqualTo(addOn.getId());

        assertThat(
                result.getItems()
                        .get(0)
                        .getAddOns()
                        .get(0)
                        .getAddOnName()
        )
                .isEqualTo("Extra Cheese");

        assertThat(
                result.getItems()
                        .get(0)
                        .getAddOns()
                        .get(0)
                        .getUnitPrice()
        )
                .isEqualByComparingTo(
                        BigDecimal.valueOf(50)
                );

        verify(orderRepository)
                .save(order);
    }
}