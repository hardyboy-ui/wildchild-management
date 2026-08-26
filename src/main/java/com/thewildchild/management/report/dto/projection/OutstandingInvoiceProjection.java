package com.thewildchild.management.report.dto.projection;

import java.math.BigDecimal;

public interface OutstandingInvoiceProjection {

    Long getOutstandingInvoiceCount();

    BigDecimal getOutstandingAmount();
}