package com.thewildchild.management.payment.repository;

import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceBillingType;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.payment.entity.Payment;
import com.thewildchild.management.payment.entity.PaymentMethod;
import com.thewildchild.management.payment.entity.PaymentStatus;
import com.thewildchild.management.report.dto.projection.DailyPaymentProjection;
import com.thewildchild.management.report.dto.projection.PaymentMethodProjection;
import com.thewildchild.management.report.dto.projection.PaymentStatusProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private com.thewildchild.management.invoice.repository.InvoiceRepository invoiceRepository;

    private Invoice invoice;

    @BeforeEach
    void setUp() {

        invoice = new Invoice();

        invoice.setInvoiceNumber(
                "TEST-INV-" + UUID.randomUUID()
        );

        invoice.setBillingType(
                InvoiceBillingType.ORDER
        );

        invoice.setSubtotal(
                new BigDecimal("1000.00")
        );

        invoice.setGrandTotal(
                new BigDecimal("1100.00")
        );

        invoice.setStatus(
                InvoiceStatus.PAID
        );

        invoice.setDiscountAmount(
                new BigDecimal("0.00")
        );

        invoice.setTaxRate(
                new BigDecimal("10.00")
        );

        invoice.setTaxAmount(
                new BigDecimal("100.00")
        );

        invoice = invoiceRepository.save(invoice);
    }


    @Test
    void shouldFindPaymentsByInvoiceIdAndStatus() {

        Payment successfulPayment = createPayment(
                PaymentMethod.CASH,
                new BigDecimal("500.00"),
                PaymentStatus.SUCCESS
        );

        Payment failedPayment = createPayment(
                PaymentMethod.QR,
                new BigDecimal("200.00"),
                PaymentStatus.FAILED
        );

        paymentRepository.save(successfulPayment);
        paymentRepository.save(failedPayment);

        List<Payment> payments =
                paymentRepository.findAllByInvoiceIdAndStatus(
                        invoice.getId(),
                        PaymentStatus.SUCCESS
                );

        assertThat(payments)
                .hasSize(1);

        assertThat(payments.get(0).getAmount())
                .isEqualByComparingTo("500.00");

        assertThat(payments.get(0).getPaymentMethod())
                .isEqualTo(PaymentMethod.CASH);

        assertThat(payments.get(0).getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);
    }


    @Test
    void shouldCalculateDailyPayments() {

        Payment cashPayment = createPayment(
                PaymentMethod.CASH,
                new BigDecimal("500.00"),
                PaymentStatus.SUCCESS
        );

        Payment qrPayment = createPayment(
                PaymentMethod.QR,
                new BigDecimal("300.00"),
                PaymentStatus.SUCCESS
        );

        Payment failedPayment = createPayment(
                PaymentMethod.CASH,
                new BigDecimal("200.00"),
                PaymentStatus.FAILED
        );

        paymentRepository.save(cashPayment);
        paymentRepository.save(qrPayment);
        paymentRepository.save(failedPayment);

        LocalDateTime start =
                LocalDateTime.now().minusMinutes(1);

        LocalDateTime end =
                LocalDateTime.now().plusMinutes(1);

        DailyPaymentProjection result =
                paymentRepository.getDailyPayments(
                        start,
                        end
                );

        assertThat(result)
                .isNotNull();

        assertThat(result.getCashPayments())
                .isEqualByComparingTo("500.00");

        assertThat(result.getQrPayments())
                .isEqualByComparingTo("300.00");

        assertThat(result.getTotalPayments())
                .isEqualByComparingTo("800.00");
    }


    @Test
    void shouldCalculatePaymentMethodSummary() {

        paymentRepository.save(
                createPayment(
                        PaymentMethod.CASH,
                        new BigDecimal("500.00"),
                        PaymentStatus.SUCCESS
                )
        );

        paymentRepository.save(
                createPayment(
                        PaymentMethod.CASH,
                        new BigDecimal("300.00"),
                        PaymentStatus.SUCCESS
                )
        );

        paymentRepository.save(
                createPayment(
                        PaymentMethod.QR,
                        new BigDecimal("400.00"),
                        PaymentStatus.SUCCESS
                )
        );

        LocalDateTime start =
                LocalDateTime.now().minusMinutes(1);

        LocalDateTime end =
                LocalDateTime.now().plusMinutes(1);

        List<PaymentMethodProjection> result =
                paymentRepository.getPaymentMethodSummary(
                        start,
                        end
                );

        assertThat(result)
                .hasSize(2);

        PaymentMethodProjection cash =
                result.stream()
                        .filter(p ->
                                p.getPaymentMethod()
                                        == PaymentMethod.CASH
                        )
                        .findFirst()
                        .orElseThrow();

        PaymentMethodProjection qr =
                result.stream()
                        .filter(p ->
                                p.getPaymentMethod()
                                        == PaymentMethod.QR
                        )
                        .findFirst()
                        .orElseThrow();

        assertThat(cash.getTransactionCount())
                .isEqualTo(2);

        assertThat(cash.getTotalAmount())
                .isEqualByComparingTo("800.00");

        assertThat(qr.getTransactionCount())
                .isEqualTo(1);

        assertThat(qr.getTotalAmount())
                .isEqualByComparingTo("400.00");
    }


    @Test
    void shouldCalculatePaymentStatusSummary() {

        paymentRepository.save(
                createPayment(
                        PaymentMethod.CASH,
                        new BigDecimal("500.00"),
                        PaymentStatus.SUCCESS
                )
        );

        paymentRepository.save(
                createPayment(
                        PaymentMethod.QR,
                        new BigDecimal("300.00"),
                        PaymentStatus.SUCCESS
                )
        );

        paymentRepository.save(
                createPayment(
                        PaymentMethod.CASH,
                        new BigDecimal("200.00"),
                        PaymentStatus.FAILED
                )
        );

        paymentRepository.save(
                createPayment(
                        PaymentMethod.QR,
                        new BigDecimal("100.00"),
                        PaymentStatus.PENDING
                )
        );

        LocalDateTime start =
                LocalDateTime.now().minusMinutes(1);

        LocalDateTime end =
                LocalDateTime.now().plusMinutes(1);

        List<PaymentStatusProjection> result =
                paymentRepository.getPaymentStatusSummary(
                        start,
                        end
                );

        assertThat(result)
                .hasSize(3);

        PaymentStatusProjection success =
                result.stream()
                        .filter(p ->
                                p.getPaymentStatus()
                                        == PaymentStatus.SUCCESS
                        )
                        .findFirst()
                        .orElseThrow();

        PaymentStatusProjection failed =
                result.stream()
                        .filter(p ->
                                p.getPaymentStatus()
                                        == PaymentStatus.FAILED
                        )
                        .findFirst()
                        .orElseThrow();

        PaymentStatusProjection pending =
                result.stream()
                        .filter(p ->
                                p.getPaymentStatus()
                                        == PaymentStatus.PENDING
                        )
                        .findFirst()
                        .orElseThrow();

        assertThat(success.getTransactionCount())
                .isEqualTo(2);

        assertThat(success.getTotalAmount())
                .isEqualByComparingTo("800.00");

        assertThat(failed.getTransactionCount())
                .isEqualTo(1);

        assertThat(failed.getTotalAmount())
                .isEqualByComparingTo("200.00");

        assertThat(pending.getTransactionCount())
                .isEqualTo(1);

        assertThat(pending.getTotalAmount())
                .isEqualByComparingTo("100.00");
    }


    private Payment createPayment(
            PaymentMethod paymentMethod,
            BigDecimal amount,
            PaymentStatus status
    ) {

        Payment payment = new Payment();

        payment.setInvoice(invoice);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setStatus(status);

        payment.setTransactionReference(
                "TXN-" + UUID.randomUUID()
        );

        return payment;
    }
}