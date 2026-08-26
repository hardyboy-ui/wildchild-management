package com.thewildchild.management.report.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OutstandingInvoiceReportResponse {

    private Long outstandingInvoiceCount;

    private BigDecimal outstandingAmount;
}