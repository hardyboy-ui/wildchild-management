package com.thewildchild.management.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {

    private UUID id;
    private String orderNumber;

    private UUID diningSessionId;

    private List<OrderItemResponse> items;
}