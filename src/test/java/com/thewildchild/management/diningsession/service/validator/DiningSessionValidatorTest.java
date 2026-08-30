package com.thewildchild.management.diningsession.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiningSessionValidatorTest {

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @InjectMocks
    private DiningSessionValidator diningSessionValidator;


    // =========================================================
    // validateCreate()
    // =========================================================

    @Test
    void shouldAllowCreateWhenTableHasNoOpenSession() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        when(diningSessionRepository.existsByTableIdAndStatus(
                tableId,
                DiningSessionStatus.OPEN
        )).thenReturn(false);

        // Act
        diningSessionValidator.validateCreate(tableId);

        // Verify
        verify(diningSessionRepository)
                .existsByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                );
    }


    @Test
    void shouldThrowExceptionWhenTableAlreadyHasOpenSession() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        when(diningSessionRepository.existsByTableIdAndStatus(
                tableId,
                DiningSessionStatus.OPEN
        )).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionValidator.validateCreate(tableId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Table already has an open dining session"
                );

        // Verify
        verify(diningSessionRepository)
                .existsByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                );
    }


    // =========================================================
    // validateOrdersCanBeClosed()
    // =========================================================

    @Test
    void shouldThrowExceptionWhenSessionHasNoOrders() {

        // Arrange
        List<Order> orders = List.of();

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionValidator.validateOrdersCanBeClosed(orders)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Dining session has no orders"
                );
    }


    @Test
    void shouldAllowClosingWhenAllOrdersAreCancelled() {

        // Arrange
        Order order = new Order();
        order.setStatus(OrderStatus.CANCELLED);

        List<Order> orders = List.of(order);

        // Act
        diningSessionValidator.validateOrdersCanBeClosed(orders);

        // No exception means success.
    }


    @Test
    void shouldAllowClosingWhenAllOrdersAreClosed() {

        // Arrange
        Order order = new Order();
        order.setStatus(OrderStatus.CLOSED);

        List<Order> orders = List.of(order);

        // Act
        diningSessionValidator.validateOrdersCanBeClosed(orders);

        // No exception means success.
    }


    @Test
    void shouldAllowClosingWhenOpenOrderIsFullyPaid() {

        // Arrange
        Order order = new Order();

        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.PAID);

        List<Order> orders = List.of(order);

        // Act
        diningSessionValidator.validateOrdersCanBeClosed(orders);

        // No exception means success.
    }


    @Test
    void shouldThrowExceptionWhenOpenOrderIsNotFullyPaid() {

        // Arrange
        Order order = new Order();

        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);
        order.setOrderNumber("ORD-001");

        List<Order> orders = List.of(order);

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionValidator.validateOrdersCanBeClosed(orders)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Dining session cannot be closed. " +
                                "Order ORD-001 is not fully paid"
                );
    }


    @Test
    void shouldAllowClosingWhenOrdersContainCancelledClosedAndPaidOpenOrders() {

        // Arrange

        Order cancelledOrder = new Order();
        cancelledOrder.setStatus(OrderStatus.CANCELLED);

        Order closedOrder = new Order();
        closedOrder.setStatus(OrderStatus.CLOSED);

        Order paidOpenOrder = new Order();
        paidOpenOrder.setStatus(OrderStatus.OPEN);
        paidOpenOrder.setBillingStatus(OrderBillingStatus.PAID);

        List<Order> orders = List.of(
                cancelledOrder,
                closedOrder,
                paidOpenOrder
        );

        // Act
        diningSessionValidator.validateOrdersCanBeClosed(orders);

        // No exception means success.
    }


    @Test
    void shouldThrowExceptionWhenAnyOpenOrderIsNotPaid() {

        // Arrange

        Order paidOrder = new Order();
        paidOrder.setStatus(OrderStatus.OPEN);
        paidOrder.setBillingStatus(OrderBillingStatus.PAID);

        Order unpaidOrder = new Order();
        unpaidOrder.setStatus(OrderStatus.OPEN);
        unpaidOrder.setBillingStatus(OrderBillingStatus.UNBILLED);
        unpaidOrder.setOrderNumber("ORD-002");

        List<Order> orders = List.of(
                paidOrder,
                unpaidOrder
        );

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionValidator.validateOrdersCanBeClosed(orders)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Dining session cannot be closed. " +
                                "Order ORD-002 is not fully paid"
                );
    }
}