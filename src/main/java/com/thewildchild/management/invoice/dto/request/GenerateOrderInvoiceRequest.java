package com.thewildchild.management.invoice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GenerateOrderInvoiceRequest {

    private UUID orderId;
}