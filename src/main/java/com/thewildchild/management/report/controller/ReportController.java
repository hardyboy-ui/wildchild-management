package com.thewildchild.management.report.controller;

import com.thewildchild.management.report.dto.response.*;
import com.thewildchild.management.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-sales")
    public ResponseEntity<DailySalesReportResponse> getDailySalesReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        return ResponseEntity.ok(
                reportService.getDailySalesReport(date)
        );
    }

    @GetMapping("/sales-summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getSalesSummary(
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodReportResponse>> getPaymentMethodReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getPaymentMethodReport(
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/payment-status")
    public ResponseEntity<List<PaymentStatusReportResponse>> getPaymentStatusReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getPaymentStatusReport(
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/outstanding-invoices")
    public ResponseEntity<OutstandingInvoiceReportResponse>
    getOutstandingInvoiceReport() {

        return ResponseEntity.ok(
                reportService.getOutstandingInvoiceReport()
        );
    }

    @GetMapping("/top-selling-menu-items")
    public ResponseEntity<List<MenuItemSalesResponse>> getTopSellingMenuItems(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getTopSellingMenuItems(
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping("/category-sales")
    public ResponseEntity<List<CategorySalesResponse>> getCategorySales(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        return ResponseEntity.ok(
                reportService.getCategorySales(
                        startDate,
                        endDate
                )
        );
    }
}