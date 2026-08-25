package com.thewildchild.management.invoice.service;

import com.thewildchild.management.invoice.dto.response.InvoiceResponse;

import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse generateOrderInvoice(UUID orderId);

    InvoiceResponse generateDiningSessionInvoice(
            UUID diningSessionId
    );

    InvoiceResponse getInvoiceById(UUID invoiceId);

    InvoiceResponse cancelInvoice(UUID invoiceId);
}