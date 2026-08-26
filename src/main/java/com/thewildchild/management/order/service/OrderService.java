package com.thewildchild.management.order.service;

import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID diningSessionId);

    OrderResponse getOrderById(UUID orderId);

    OrderResponse addOrderItem(
            UUID orderId,
            AddOrderItemRequest request
    );

    OrderResponse closeOrder(UUID orderId);
}
