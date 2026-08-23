package com.thewildchild.management.order.service.mapper;

import com.thewildchild.management.order.dto.response.OrderItemAddOnResponse;
import com.thewildchild.management.order.entity.OrderItemAddOn;
import org.springframework.stereotype.Component;

@Component
public class OrderItemAddOnMapper {

    public OrderItemAddOnResponse toResponse(OrderItemAddOn entity) {

        OrderItemAddOnResponse response = new OrderItemAddOnResponse();

        response.setId(entity.getId());

        if (entity.getAddOn() != null) {
            response.setAddOnId(entity.getAddOn().getId());
            response.setAddOnName(entity.getAddOn().getName());
        }

        response.setUnitPrice(entity.getUnitPrice());

        return response;
    }
}