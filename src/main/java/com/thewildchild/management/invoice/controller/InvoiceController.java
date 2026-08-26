package com.thewildchild.management.invoice.controller;

import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.service.InvoiceService;
import jakarta.validation.Valid;
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
            @PathVariable UUID orderId,
            @Valid @RequestBody GenerateInvoiceRequest request
    ) {

        return ResponseEntity.ok(
                invoiceService.generateOrderInvoice(
                        orderId,
                        request
                )
        );
    }

    @PostMapping("/dining-session/{diningSessionId}")
    public ResponseEntity<InvoiceResponse> generateDiningSessionInvoice(
            @PathVariable UUID diningSessionId,
            @Valid @RequestBody GenerateInvoiceRequest request
    ) {

        return ResponseEntity.ok(
                invoiceService.generateDiningSessionInvoice(
                        diningSessionId,
                        request
                )
        );
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