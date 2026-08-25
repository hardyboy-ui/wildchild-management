package com.thewildchild.management.payment.dto.response;

import com.thewildchild.management.payment.entity.PaymentMethod;
import com.thewildchild.management.payment.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {

    private UUID id;

    private UUID invoiceId;

    private PaymentMethod paymentMethod;

    private BigDecimal amount;

    private PaymentStatus status;

    private String transactionReference;

    private LocalDateTime createdAt;
}