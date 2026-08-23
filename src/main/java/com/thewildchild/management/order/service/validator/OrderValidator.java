package com.thewildchild.management.order.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validateMenuItem(MenuItem menuItem) {

        if (menuItem == null) {
            throw new BusinessException("Menu item not found");
        }

        if (!menuItem.isActive()) {
            throw new BusinessException(
                    "Menu item is not active"
            );
        }
    }

    public void validateAddOn(AddOn addOn) {

        if (addOn == null) {
            throw new BusinessException("Add-on not found");
        }

        if (!addOn.isActive()) {
            throw new BusinessException(
                    "Add-on is not active"
            );
        }
    }

    public void validateOrderItemRequest(
            AddOrderItemRequest request
    ) {

        if (request.getQuantity() == null ||
                request.getQuantity() < 1) {

            throw new BusinessException(
                    "Order item quantity must be at least 1"
            );
        }
    }
}