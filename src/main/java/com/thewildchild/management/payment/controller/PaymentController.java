package com.thewildchild.management.payment.controller;

import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.dto.response.PaymentResponse;
import com.thewildchild.management.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/invoice/{invoiceId}")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.createPayment(
                        invoiceId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable UUID paymentId
    ) {

        PaymentResponse response =
                paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(response);
    }
}