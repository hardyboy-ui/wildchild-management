package com.thewildchild.management.report.service;

import com.thewildchild.management.report.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    DailySalesReportResponse getDailySalesReport(
            LocalDate date
    );

    SalesSummaryResponse getSalesSummary(
            LocalDate startDate,
            LocalDate endDate
    );

    List<PaymentMethodReportResponse> getPaymentMethodReport(
            LocalDate startDate,
            LocalDate endDate
    );

    List<PaymentStatusReportResponse> getPaymentStatusReport(
            LocalDate startDate,
            LocalDate endDate
    );

    OutstandingInvoiceReportResponse getOutstandingInvoiceReport();

    List<MenuItemSalesResponse> getTopSellingMenuItems(
            LocalDate startDate,
            LocalDate endDate
    );

    List<CategorySalesResponse> getCategorySales(
            LocalDate startDate,
            LocalDate endDate
    );
}