package com.thewildchild.management.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class KotItemResponse {

    private UUID orderItemId;

    private UUID menuItemId;
    private String menuItemName;

    private Integer quantity;

    private List<OrderItemAddOnResponse> addOns;

    private String specialInstructions;
}