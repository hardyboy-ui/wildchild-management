package com.thewildchild.management.report.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.order.repository.OrderItemRepository;
import com.thewildchild.management.payment.entity.Payment;
import com.thewildchild.management.payment.entity.PaymentStatus;
import com.thewildchild.management.payment.repository.PaymentRepository;
import com.thewildchild.management.report.dto.projection.*;
import com.thewildchild.management.report.dto.response.*;
import com.thewildchild.management.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public DailySalesReportResponse getDailySalesReport(
            LocalDate date
    ) {

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.plusDays(1).atStartOfDay();

        InvoiceSalesProjection invoiceSales =
                invoiceRepository.getSalesBetween(
                        InvoiceStatus.PAID,
                        start,
                        end
                );

        DailyPaymentProjection paymentSales =
                paymentRepository.getDailyPayments(
                        start,
                        end
                );

        long totalInvoices =
                invoiceRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );

        long paidInvoices =
                invoiceRepository
                        .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                InvoiceStatus.PAID,
                                start,
                                end
                        );

        long cancelledInvoices =
                invoiceRepository
                        .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                InvoiceStatus.CANCELLED,
                                start,
                                end
                        );

        DailySalesReportResponse response =
                new DailySalesReportResponse();

        response.setDate(date);

        response.setTotalInvoices(totalInvoices);
        response.setPaidInvoices(paidInvoices);
        response.setCancelledInvoices(cancelledInvoices);

        response.setGrossSales(
                invoiceSales.getGrossSales()
        );

        response.setTotalDiscount(
                invoiceSales.getTotalDiscount()
        );

        response.setTotalTax(
                invoiceSales.getTotalTax()
        );

        response.setNetSales(
                invoiceSales.getNetSales()
        );

        response.setCashPayments(
                paymentSales.getCashPayments()
        );

        response.setQrPayments(
                paymentSales.getQrPayments()
        );

        response.setTotalPayments(
                paymentSales.getTotalPayments()
        );

        return response;
    }

    @Override
    public SalesSummaryResponse getSalesSummary(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1)
                        .atStartOfDay();

        InvoiceSalesProjection invoiceSales =
                invoiceRepository.getSalesBetween(
                        InvoiceStatus.PAID,
                        start,
                        end
                );

        DailyPaymentProjection paymentSales =
                paymentRepository.getDailyPayments(
                        start,
                        end
                );

        long totalInvoices =
                invoiceRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );

        long paidInvoices =
                invoiceRepository
                        .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                InvoiceStatus.PAID,
                                start,
                                end
                        );

        long cancelledInvoices =
                invoiceRepository
                        .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                InvoiceStatus.CANCELLED,
                                start,
                                end
                        );

        SalesSummaryResponse response =
                new SalesSummaryResponse();

        response.setStartDate(startDate);
        response.setEndDate(endDate);

        response.setTotalInvoices(totalInvoices);
        response.setPaidInvoices(paidInvoices);
        response.setCancelledInvoices(cancelledInvoices);

        response.setGrossSales(
                invoiceSales.getGrossSales()
        );

        response.setTotalDiscount(
                invoiceSales.getTotalDiscount()
        );

        response.setTotalTax(
                invoiceSales.getTotalTax()
        );

        response.setNetSales(
                invoiceSales.getNetSales()
        );

        response.setCashPayments(
                paymentSales.getCashPayments()
        );

        response.setQrPayments(
                paymentSales.getQrPayments()
        );

        response.setTotalPayments(
                paymentSales.getTotalPayments()
        );

        return response;
    }

    @Override
    public List<PaymentMethodReportResponse> getPaymentMethodReport(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1)
                        .atStartOfDay();

        List<PaymentMethodProjection> projections =
                paymentRepository.getPaymentMethodSummary(
                        start,
                        end
                );

        return projections.stream()
                .map(projection -> {

                    PaymentMethodReportResponse response =
                            new PaymentMethodReportResponse();

                    response.setPaymentMethod(
                            projection.getPaymentMethod()
                    );

                    response.setTransactionCount(
                            projection.getTransactionCount()
                    );

                    response.setTotalAmount(
                            projection.getTotalAmount()
                    );

                    return response;
                })
                .toList();
    }

    @Override
    public List<PaymentStatusReportResponse> getPaymentStatusReport(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1)
                        .atStartOfDay();

        List<PaymentStatusProjection> projections =
                paymentRepository.getPaymentStatusSummary(
                        start,
                        end
                );

        return projections.stream()
                .map(projection -> {

                    PaymentStatusReportResponse response =
                            new PaymentStatusReportResponse();

                    response.setPaymentStatus(
                            projection.getPaymentStatus()
                    );

                    response.setTransactionCount(
                            projection.getTransactionCount()
                    );

                    response.setTotalAmount(
                            projection.getTotalAmount()
                    );

                    return response;
                })
                .toList();
    }

    @Override
    public OutstandingInvoiceReportResponse getOutstandingInvoiceReport() {

        List<Invoice> outstandingInvoices =
                invoiceRepository.findOutstandingInvoices();

        long outstandingInvoiceCount =
                outstandingInvoices.size();

        BigDecimal outstandingAmount =
                BigDecimal.ZERO;

        for (Invoice invoice : outstandingInvoices) {

            List<Payment> successfulPayments =
                    paymentRepository
                            .findAllByInvoiceIdAndStatus(
                                    invoice.getId(),
                                    PaymentStatus.SUCCESS
                            );

            BigDecimal paidAmount =
                    successfulPayments.stream()
                            .map(Payment::getAmount)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            BigDecimal invoiceOutstanding =
                    invoice.getGrandTotal()
                            .subtract(paidAmount);

            outstandingAmount =
                    outstandingAmount.add(
                            invoiceOutstanding
                    );
        }

        OutstandingInvoiceReportResponse response =
                new OutstandingInvoiceReportResponse();

        response.setOutstandingInvoiceCount(
                outstandingInvoiceCount
        );

        response.setOutstandingAmount(
                outstandingAmount
        );

        return response;
    }

    @Override
    public List<MenuItemSalesResponse> getTopSellingMenuItems(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1)
                        .atStartOfDay();

        List<MenuItemSalesProjection> projections =
                orderItemRepository.getTopSellingMenuItems(
                        start,
                        end
                );

        return projections.stream()
                .map(projection -> {

                    MenuItemSalesResponse response =
                            new MenuItemSalesResponse();

                    response.setMenuItemId(
                            projection.getMenuItemId()
                    );

                    response.setMenuItemName(
                            projection.getMenuItemName()
                    );

                    response.setQuantitySold(
                            projection.getQuantitySold()
                    );

                    response.setRevenue(
                            projection.getRevenue()
                    );

                    return response;
                })
                .toList();
    }

    @Override
    public List<CategorySalesResponse> getCategorySales(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.plusDays(1)
                        .atStartOfDay();

        List<CategorySalesProjection> projections =
                orderItemRepository.getCategorySales(
                        start,
                        end
                );

        return projections.stream()
                .map(projection -> {

                    CategorySalesResponse response =
                            new CategorySalesResponse();

                    response.setCategoryId(
                            projection.getCategoryId()
                    );

                    response.setCategoryName(
                            projection.getCategoryName()
                    );

                    response.setQuantitySold(
                            projection.getQuantitySold()
                    );

                    response.setRevenue(
                            projection.getRevenue()
                    );

                    return response;
                })
                .toList();
    }
}