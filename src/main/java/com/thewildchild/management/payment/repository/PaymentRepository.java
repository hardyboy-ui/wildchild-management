package com.thewildchild.management.payment.repository;

import com.thewildchild.management.payment.entity.Payment;
import com.thewildchild.management.payment.entity.PaymentStatus;
import com.thewildchild.management.report.dto.projection.DailyPaymentProjection;
import com.thewildchild.management.report.dto.projection.PaymentMethodProjection;
import com.thewildchild.management.report.dto.projection.PaymentStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.invoice.id = :invoiceId
      AND p.status =
          com.thewildchild.management.payment.entity.PaymentStatus.SUCCESS
    """)
    BigDecimal getTotalSuccessfulPayments(
            @Param("invoiceId") UUID invoiceId
    );

    List<Payment> findAllByInvoiceIdAndStatus(
            UUID invoiceId,
            PaymentStatus status
    );

    @Query("""
        SELECT
            COALESCE(
                SUM(
                    CASE
                        WHEN p.paymentMethod = com.thewildchild.management.payment.entity.PaymentMethod.CASH
                        THEN p.amount
                        ELSE 0
                    END
                ), 0
            ) AS cashPayments,

            COALESCE(
                SUM(
                    CASE
                        WHEN p.paymentMethod = com.thewildchild.management.payment.entity.PaymentMethod.QR
                        THEN p.amount
                        ELSE 0
                    END
                ), 0
            ) AS qrPayments,

            COALESCE(SUM(p.amount), 0) AS totalPayments

        FROM Payment p

        WHERE p.status =
              com.thewildchild.management.payment.entity.PaymentStatus.SUCCESS

          AND p.createdAt >= :start
          AND p.createdAt < :end
        """)
    DailyPaymentProjection getDailyPayments(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT
            p.paymentMethod AS paymentMethod,
            COUNT(p) AS transactionCount,
            COALESCE(SUM(p.amount), 0) AS totalAmount
        FROM Payment p
        WHERE p.status =
              com.thewildchild.management.payment.entity.PaymentStatus.SUCCESS
          AND p.createdAt >= :start
          AND p.createdAt < :end
        GROUP BY p.paymentMethod
        ORDER BY p.paymentMethod
        """)
    List<PaymentMethodProjection> getPaymentMethodSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT
            p.status AS paymentStatus,
            COUNT(p) AS transactionCount,
            COALESCE(SUM(p.amount), 0) AS totalAmount
        FROM Payment p
        WHERE p.createdAt >= :start
          AND p.createdAt < :end
        GROUP BY p.status
        ORDER BY p.status
        """)
    List<PaymentStatusProjection> getPaymentStatusSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}