package com.thewildchild.management.report.dto.projection;

import com.thewildchild.management.payment.entity.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentMethodProjection {

    PaymentMethod getPaymentMethod();

    Long getTransactionCount();

    BigDecimal getTotalAmount();
}