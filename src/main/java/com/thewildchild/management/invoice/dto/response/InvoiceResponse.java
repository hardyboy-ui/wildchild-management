package com.thewildchild.management.invoice.dto.response;

import com.thewildchild.management.invoice.entity.InvoiceBillingType;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InvoiceResponse {

    private UUID id;

    private String invoiceNumber;

    private InvoiceBillingType billingType;

    private List<InvoiceOrderResponse> orders;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal grandTotal;

    private InvoiceStatus status;

    private LocalDateTime createdAt;
}