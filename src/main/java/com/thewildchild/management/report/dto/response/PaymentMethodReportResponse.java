package com.thewildchild.management.report.dto.response;

import com.thewildchild.management.payment.entity.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentMethodReportResponse {

    private PaymentMethod paymentMethod;

    private Long transactionCount;

    private BigDecimal totalAmount;
}