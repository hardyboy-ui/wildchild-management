package com.thewildchild.management.order.service.mapper;

import com.thewildchild.management.order.dto.response.OrderItemResponse;
import com.thewildchild.management.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final OrderItemAddOnMapper orderItemAddOnMapper;

    public OrderItemResponse toResponse(OrderItem entity) {

        OrderItemResponse response = new OrderItemResponse();

        response.setId(entity.getId());

        if (entity.getMenuItem() != null) {
            response.setMenuItemId(entity.getMenuItem().getId());
            response.setMenuItemName(entity.getMenuItem().getName());
        }

        response.setQuantity(entity.getQuantity());
        response.setKotSentQuantity(entity.getKotSentQuantity());
        response.setUnitPrice(entity.getUnitPrice());
        response.setSpecialInstructions(
                entity.getSpecialInstructions()
        );

        if (entity.getAddOns() != null) {
            response.setAddOns(
                    entity.getAddOns()
                            .stream()
                            .map(orderItemAddOnMapper::toResponse)
                            .toList()
            );
        }

        return response;
    }
}