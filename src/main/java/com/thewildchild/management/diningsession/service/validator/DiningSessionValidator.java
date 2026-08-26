package com.thewildchild.management.diningsession.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DiningSessionValidator {

    private final DiningSessionRepository diningSessionRepository;

    public void validateCreate(UUID tableId) {

        if (diningSessionRepository.existsByTableIdAndStatus(
                tableId,
                DiningSessionStatus.OPEN
        )) {
            throw new BusinessException(
                    "Table already has an open dining session"
            );
        }
    }

    public void validateOrdersCanBeClosed(
            List<Order> orders
    ) {

        if (orders.isEmpty()) {
            throw new BusinessException(
                    "Dining session has no orders"
            );
        }

        for (Order order : orders) {

            // Already cancelled → nothing to validate
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }

            // Already closed → nothing to validate
            if (order.getStatus() == OrderStatus.CLOSED) {
                continue;
            }

            // Every OPEN order must be fully paid
            if (order.getBillingStatus()
                    != OrderBillingStatus.PAID) {

                throw new BusinessException(
                        "Dining session cannot be closed. " +
                                "Order " +
                                order.getOrderNumber() +
                                " is not fully paid"
                );
            }
        }
    }
}