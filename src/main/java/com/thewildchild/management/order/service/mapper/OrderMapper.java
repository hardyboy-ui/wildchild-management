package com.thewildchild.management.order.service.mapper;

import com.thewildchild.management.order.dto.response.OrderResponse;
import com.thewildchild.management.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order entity) {

        OrderResponse response = new OrderResponse();

        response.setId(entity.getId());
        response.setOrderNumber(entity.getOrderNumber());

        if (entity.getDiningSession() != null) {
            response.setDiningSessionId(
                    entity.getDiningSession().getId()
            );
        }

        if (entity.getItems() != null) {
            response.setItems(
                    entity.getItems()
                            .stream()
                            .map(orderItemMapper::toResponse)
                            .toList()
            );
        }

        return response;
    }
}