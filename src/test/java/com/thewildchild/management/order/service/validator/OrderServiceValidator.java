package com.thewildchild.management.order.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.order.dto.request.AddOrderItemAddOnRequest;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderValidatorTest {

    private final OrderValidator orderValidator =
            new OrderValidator();


    // =========================================================
    // validateRequest()
    // =========================================================

    @Test
    void shouldAllowValidOrderItemRequest() {

        // Arrange
        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(2);

        // Act
        orderValidator.validateRequest(request);

        // No exception means success.
    }


    @Test
    void shouldThrowExceptionWhenQuantityIsNull() {

        // Arrange
        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(null);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Quantity must be at least 1"
                );
    }


    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {

        // Arrange
        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(0);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Quantity must be at least 1"
                );
    }


    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {

        // Arrange
        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(-1);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Quantity must be at least 1"
                );
    }


    @Test
    void shouldAllowRequestWhenAddOnsAreNull() {

        // Arrange
        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(1);
        request.setAddOns(null);

        // Act
        orderValidator.validateRequest(request);

        // No exception means success.
    }


    @Test
    void shouldAllowRequestWhenAddOnsAreUnique() {

        // Arrange
        UUID addOnId1 = UUID.randomUUID();
        UUID addOnId2 = UUID.randomUUID();

        AddOrderItemAddOnRequest addOn1 =
                new AddOrderItemAddOnRequest();

        addOn1.setAddOnId(addOnId1);

        AddOrderItemAddOnRequest addOn2 =
                new AddOrderItemAddOnRequest();

        addOn2.setAddOnId(addOnId2);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(1);
        request.setAddOns(List.of(addOn1, addOn2));

        // Act
        orderValidator.validateRequest(request);

        // No exception means success.
    }


    @Test
    void shouldThrowExceptionWhenDuplicateAddOnExists() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOrderItemAddOnRequest addOn1 =
                new AddOrderItemAddOnRequest();

        addOn1.setAddOnId(addOnId);

        AddOrderItemAddOnRequest addOn2 =
                new AddOrderItemAddOnRequest();

        addOn2.setAddOnId(addOnId);

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(1);
        request.setAddOns(List.of(addOn1, addOn2));

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Duplicate add-on is not allowed: "
                                + addOnId
                );
    }


    // =========================================================
    // validateCanBeClosed()
    // =========================================================

    @Test
    void shouldAllowClosingPaidOpenOrder() {

        // Arrange
        Order order =
                new Order();

        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.PAID);

        // Act
        orderValidator.validateCanBeClosed(order);

        // No exception means success.
    }


    @Test
    void shouldThrowExceptionWhenOrderIsAlreadyClosed() {

        // Arrange
        Order order =
                new Order();

        order.setStatus(OrderStatus.CLOSED);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateCanBeClosed(order)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Order is already closed"
                );
    }


    @Test
    void shouldThrowExceptionWhenOrderIsCancelled() {

        // Arrange
        Order order =
                new Order();

        order.setStatus(OrderStatus.CANCELLED);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateCanBeClosed(order)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Cancelled order cannot be closed"
                );
    }


    @Test
    void shouldThrowExceptionWhenOrderIsNotFullyPaid() {

        // Arrange
        Order order =
                new Order();

        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);

        // Act & Assert
        assertThatThrownBy(() ->
                orderValidator.validateCanBeClosed(order)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Order cannot be closed until it is fully paid"
                );
    }
}