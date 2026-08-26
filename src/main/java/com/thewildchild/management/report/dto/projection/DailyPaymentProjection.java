package com.thewildchild.management.report.dto.projection;

import java.math.BigDecimal;

public interface DailyPaymentProjection {

    BigDecimal getCashPayments();

    BigDecimal getQrPayments();

    BigDecimal getTotalPayments();
}