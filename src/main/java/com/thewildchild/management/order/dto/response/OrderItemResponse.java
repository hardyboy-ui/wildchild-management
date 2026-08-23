package com.thewildchild.management.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderItemResponse {

    private UUID id;

    private UUID menuItemId;
    private String menuItemName;

    private Integer quantity;
    private Integer kotSentQuantity;

    private BigDecimal unitPrice;

    private String specialInstructions;

    private List<OrderItemAddOnResponse> addOns;
}