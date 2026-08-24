package com.thewildchild.management.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class KotResponse {

    private UUID orderId;
    private String orderNumber;
    private List<KotItemResponse> items;
}