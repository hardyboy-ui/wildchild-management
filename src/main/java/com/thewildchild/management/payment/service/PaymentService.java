package com.thewildchild.management.payment.service;

import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(
            UUID invoiceId,
            CreatePaymentRequest request
    );

    PaymentResponse getPaymentById(
            UUID paymentId
    );
}