package com.thewildchild.management.invoice.service;

import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;

import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse generateOrderInvoice(UUID orderId,
                                         GenerateInvoiceRequest request);

    InvoiceResponse generateDiningSessionInvoice(
            UUID diningSessionId,
            GenerateInvoiceRequest request
    );

    InvoiceResponse getInvoiceById(UUID invoiceId);

    InvoiceResponse cancelInvoice(UUID invoiceId);
}