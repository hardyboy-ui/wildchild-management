package com.thewildchild.management.order.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.order.dto.request.AddOrderItemAddOnRequest;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class OrderValidator {

    public void validateRequest(
            AddOrderItemRequest request
    ) {

        if (request.getQuantity() == null
                || request.getQuantity() < 1) {

            throw new BusinessException(
                    "Quantity must be at least 1"
            );
        }

        if (request.getAddOns() == null) {
            return;
        }

        // Specific checks for addOns
        Set<UUID> addOnIds = new HashSet<>();

        for (AddOrderItemAddOnRequest addOnRequest
                : request.getAddOns()) {

            if (!addOnIds.add(addOnRequest.getAddOnId())) {

                throw new BusinessException(
                        "Duplicate add-on is not allowed: "
                                + addOnRequest.getAddOnId()
                );
            }
        }
    }

    public void validateCanBeClosed(Order order) {

        if (order.getStatus() == OrderStatus.CLOSED) {
            throw new BusinessException(
                    "Order is already closed"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(
                    "Cancelled order cannot be closed"
            );
        }

        if (order.getBillingStatus() != OrderBillingStatus.PAID) {
            throw new BusinessException(
                    "Order cannot be closed until it is fully paid"
            );
        }
    }
}