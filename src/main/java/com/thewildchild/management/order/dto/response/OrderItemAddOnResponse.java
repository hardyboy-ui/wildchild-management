package com.thewildchild.management.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemAddOnResponse {

    private UUID id;
    private UUID addOnId;
    private String addOnName;
    private BigDecimal unitPrice;
}