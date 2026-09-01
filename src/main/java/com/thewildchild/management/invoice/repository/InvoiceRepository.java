package com.thewildchild.management.invoice.repository;

import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.report.dto.projection.InvoiceSalesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InvoiceRepository
        extends JpaRepository<Invoice, UUID> {


    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );

    long countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            InvoiceStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT
            COALESCE(SUM(i.subtotal), 0) AS grossSales,
            COALESCE(SUM(i.discountAmount), 0) AS totalDiscount,
            COALESCE(SUM(i.taxAmount), 0) AS totalTax,
            COALESCE(SUM(i.grandTotal), 0) AS netSales
        FROM Invoice i
        WHERE i.status = :status
          AND i.createdAt >= :start
          AND i.createdAt < :end
        """)
    InvoiceSalesProjection getSalesBetween(
            @Param("status") InvoiceStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT i
        FROM Invoice i
        WHERE i.status IN (
            com.thewildchild.management.invoice.entity.InvoiceStatus.GENERATED,
            com.thewildchild.management.invoice.entity.InvoiceStatus.PARTIALLY_PAID
        )
        """)
    List<Invoice> findOutstandingInvoices();
}