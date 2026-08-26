package com.thewildchild.management.report.dto.projection;

import java.math.BigDecimal;

public interface InvoiceSalesProjection {

    BigDecimal getGrossSales();

    BigDecimal getTotalDiscount();

    BigDecimal getTotalTax();

    BigDecimal getNetSales();
}