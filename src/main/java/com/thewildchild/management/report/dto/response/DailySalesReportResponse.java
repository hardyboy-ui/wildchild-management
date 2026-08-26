package com.thewildchild.management.report.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailySalesReportResponse {

    private LocalDate date;

    private Long totalInvoices;

    private Long paidInvoices;

    private Long cancelledInvoices;

    private BigDecimal grossSales;

    private BigDecimal totalDiscount;

    private BigDecimal totalTax;

    private BigDecimal netSales;

    private BigDecimal cashPayments;

    private BigDecimal qrPayments;

    private BigDecimal totalPayments;
}