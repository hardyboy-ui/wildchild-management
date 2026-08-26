package com.thewildchild.management.invoice.dto.response;

import com.thewildchild.management.invoice.entity.DiscountType;
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

    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal discountAmount;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal grandTotal;

    private InvoiceStatus status;

    private LocalDateTime createdAt;
}