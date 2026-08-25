package com.thewildchild.management.invoice.controller;

import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<InvoiceResponse> generateOrderInvoice(
            @PathVariable UUID orderId
    ) {

        InvoiceResponse response =
                invoiceService.generateOrderInvoice(orderId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/dining-session/{diningSessionId}")
    public ResponseEntity<InvoiceResponse> generateDiningSessionInvoice(
            @PathVariable UUID diningSessionId
    ) {

        InvoiceResponse response =
                invoiceService.generateDiningSessionInvoice(
                        diningSessionId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable UUID invoiceId
    ) {

        InvoiceResponse response =
                invoiceService.getInvoiceById(invoiceId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{invoiceId}/cancel")
    public ResponseEntity<InvoiceResponse> cancelInvoice(
            @PathVariable UUID invoiceId
    ) {

        InvoiceResponse response =
                invoiceService.cancelInvoice(invoiceId);

        return ResponseEntity.ok(response);
    }
}