package com.thewildchild.management.invoice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InvoiceOrderResponse {

    private UUID orderId;

    private String orderNumber;
}