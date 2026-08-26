package com.thewildchild.management.report.dto.projection;

import com.thewildchild.management.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentStatusProjection {

    PaymentStatus getPaymentStatus();

    Long getTransactionCount();

    BigDecimal getTotalAmount();
}