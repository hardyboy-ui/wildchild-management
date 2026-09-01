package com.thewildchild.management.payment.service;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.invoice.entity.*;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.invoice.repository.InvoiceOrderRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.dto.response.PaymentResponse;
import com.thewildchild.management.payment.entity.PaymentMethod;
import com.thewildchild.management.payment.entity.PaymentStatus;
import com.thewildchild.management.payment.repository.PaymentRepository;
import com.thewildchild.management.payment.service.impl.PaymentServiceImpl;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceOrderRepository invoiceOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;


    private Invoice invoice;
    private Order order;


    @BeforeEach
    void setUp() {
        CafeTable table = new CafeTable();

        table.setTableNumber(
                "T-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        table.setCapacity(4);
        table.setActive(true);
        table.setDisplayOrder(1);

        table = cafeTableRepository.saveAndFlush(table);


        /*
         * ---------------------------------------------------------
         * DINING SESSION
         * ---------------------------------------------------------
         */

        DiningSession diningSession =
                new DiningSession();

        diningSession.setSessionNumber(
                "DS-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        diningSession.setTable(table);

        diningSession.setStatus(
                DiningSessionStatus.OPEN
        );
        diningSession.setOpenedAt(LocalDateTime.now());

        diningSession =
                diningSessionRepository.saveAndFlush(
                        diningSession
                );


        /*
         * ---------------------------------------------------------
         * ORDER
         * ---------------------------------------------------------
         */

        order = new Order();

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        order.setDiningSession(diningSession);

        order.setStatus(
                OrderStatus.CLOSED
        );

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        order = orderRepository.saveAndFlush(order);


        /*
         * ---------------------------------------------------------
         * INVOICE
         * ---------------------------------------------------------
         */

        invoice = new Invoice();

        invoice.setInvoiceNumber(
                "INV-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        invoice.setBillingType(
                InvoiceBillingType.ORDER
        );

        invoice.setSubtotal(
                new BigDecimal("200.00")
        );

        invoice.setDiscountType(null);

        invoice.setDiscountValue(
                BigDecimal.ZERO
        );

        invoice.setDiscountAmount(
                BigDecimal.ZERO
        );

        invoice.setTaxRate(
                BigDecimal.ZERO
        );

        invoice.setTaxAmount(
                BigDecimal.ZERO
        );

        invoice.setGrandTotal(
                new BigDecimal("200.00")
        );

        invoice.setStatus(
                InvoiceStatus.GENERATED
        );

        invoice =
                invoiceRepository.saveAndFlush(invoice);

        /*
         * ---------------------------------------------------------
         * INVOICE ORDER
         * ---------------------------------------------------------
         */

        InvoiceOrder invoiceOrder = new InvoiceOrder();

        invoiceOrder.setInvoice(invoice);
        invoiceOrder.setOrder(order);

        invoice.getInvoiceOrders()
                .add(invoiceOrder);

        invoiceOrderRepository.saveAndFlush(invoiceOrder);
    }


    // ============================================================
    // CREATE PAYMENT
    // ============================================================

    @Test
    void shouldCreateSuccessfulPayment() {

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(
                PaymentMethod.CASH
        );

        request.setAmount(
                new BigDecimal("100.00")
        );

        request.setTransactionReference(
                "TXN-001"
        );


        PaymentResponse response =
                paymentService.createPayment(
                        invoice.getId(),
                        request
                );


        assertThat(response)
                .isNotNull();

        assertThat(response.getId())
                .isNotNull();

        assertThat(response.getInvoiceId())
                .isEqualTo(invoice.getId());

        assertThat(response.getPaymentMethod())
                .isEqualTo(PaymentMethod.CASH);

        assertThat(response.getAmount())
                .isEqualByComparingTo("100.00");

        assertThat(response.getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);

        assertThat(response.getTransactionReference())
                .isEqualTo("TXN-001");


        /*
         * Invoice should now be partially paid.
         */

        Invoice updatedInvoice =
                invoiceRepository.findById(invoice.getId())
                        .orElseThrow();

        assertThat(updatedInvoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.PARTIALLY_PAID
                );


        /*
         * Order should still be billed,
         * because invoice is not fully paid.
         */

        Order updatedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(updatedOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.BILLED
                );
    }


    // ============================================================
    // FULL PAYMENT
    // ============================================================

    @Test
    void shouldMarkInvoiceAsPaidWhenFullAmountIsPaid() {

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(
                PaymentMethod.CASH
        );

        request.setAmount(
                new BigDecimal("200.00")
        );

        request.setTransactionReference(
                "TXN-FULL"
        );


        PaymentResponse response =
                paymentService.createPayment(
                        invoice.getId(),
                        request
                );


        assertThat(response.getStatus())
                .isEqualTo(
                        PaymentStatus.SUCCESS
                );


        /*
         * Invoice should be PAID.
         */

        Invoice updatedInvoice =
                invoiceRepository.findById(invoice.getId())
                        .orElseThrow();

        assertThat(updatedInvoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.PAID
                );


        /*
         * Associated order should be PAID.
         */

        Order updatedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(updatedOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.PAID
                );
    }


    // ============================================================
    // PAYMENT EXCEEDING REMAINING AMOUNT
    // ============================================================

    @Test
    void shouldRejectPaymentExceedingRemainingAmount() {

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(
                PaymentMethod.CASH
        );

        request.setAmount(
                new BigDecimal("250.00")
        );

        request.setTransactionReference(
                "TXN-EXCEED"
        );


        assertThatThrownBy(() ->
                paymentService.createPayment(
                        invoice.getId(),
                        request
                )
        )
                .isInstanceOf(
                        BusinessException.class
                )
                .hasMessage(
                        "Payment amount cannot exceed remaining invoice amount"
                );


        /*
         * No payment should have been created.
         */

        assertThat(
                paymentRepository
                        .findAllByInvoiceIdAndStatus(
                                invoice.getId(),
                                PaymentStatus.SUCCESS
                        )
        )
                .isEmpty();


        /*
         * Invoice should remain GENERATED.
         */

        Invoice unchangedInvoice =
                invoiceRepository.findById(invoice.getId())
                        .orElseThrow();

        assertThat(unchangedInvoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.GENERATED
                );
    }


    // ============================================================
    // NON-EXISTENT INVOICE
    // ============================================================

    @Test
    void shouldThrowExceptionWhenInvoiceDoesNotExist() {

        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(
                PaymentMethod.CASH
        );

        request.setAmount(
                new BigDecimal("100.00")
        );


        assertThatThrownBy(() ->
                paymentService.createPayment(
                        invoiceId,
                        request
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Invoice not found with id: " + invoiceId
                );
    }


    // ============================================================
    // GET PAYMENT
    // ============================================================

    @Test
    void shouldGetPaymentById() {

        /*
         * First create payment.
         */

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(
                PaymentMethod.QR
        );

        request.setAmount(
                new BigDecimal("100.00")
        );

        request.setTransactionReference(
                "TXN-GET"
        );


        PaymentResponse created =
                paymentService.createPayment(
                        invoice.getId(),
                        request
                );


        /*
         * Retrieve payment.
         */

        PaymentResponse response =
                paymentService.getPaymentById(
                        created.getId()
                );


        assertThat(response)
                .isNotNull();

        assertThat(response.getId())
                .isEqualTo(
                        created.getId()
                );

        assertThat(response.getInvoiceId())
                .isEqualTo(
                        invoice.getId()
                );

        assertThat(response.getPaymentMethod())
                .isEqualTo(
                        PaymentMethod.QR
                );

        assertThat(response.getAmount())
                .isEqualByComparingTo(
                        "100.00"
                );

        assertThat(response.getStatus())
                .isEqualTo(
                        PaymentStatus.SUCCESS
                );
    }


    // ============================================================
    // PAYMENT NOT FOUND
    // ============================================================

    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist() {

        UUID paymentId =
                UUID.randomUUID();


        assertThatThrownBy(() ->
                paymentService.getPaymentById(
                        paymentId
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Payment not found with id: " + paymentId
                );
    }


    // ============================================================
    // PARTIAL PAYMENTS
    // ============================================================

    @Test
    void shouldMarkInvoiceAsPaidAfterMultiplePayments() {

        /*
         * First payment = 100
         */

        CreatePaymentRequest firstPayment =
                new CreatePaymentRequest();

        firstPayment.setPaymentMethod(
                PaymentMethod.CASH
        );

        firstPayment.setAmount(
                new BigDecimal("100.00")
        );

        firstPayment.setTransactionReference(
                "TXN-PART-1"
        );


        paymentService.createPayment(
                invoice.getId(),
                firstPayment
        );


        /*
         * Invoice should be partially paid.
         */

        Invoice partiallyPaidInvoice =
                invoiceRepository.findById(
                        invoice.getId()
                ).orElseThrow();

        assertThat(partiallyPaidInvoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.PARTIALLY_PAID
                );


        /*
         * Second payment = remaining 100.
         */

        CreatePaymentRequest secondPayment =
                new CreatePaymentRequest();

        secondPayment.setPaymentMethod(
                PaymentMethod.QR
        );

        secondPayment.setAmount(
                new BigDecimal("100.00")
        );

        secondPayment.setTransactionReference(
                "TXN-PART-2"
        );


        paymentService.createPayment(
                invoice.getId(),
                secondPayment
        );


        /*
         * Invoice should now be fully paid.
         */

        Invoice paidInvoice =
                invoiceRepository.findById(
                        invoice.getId()
                ).orElseThrow();

        assertThat(paidInvoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.PAID
                );


        /*
         * Order should now be PAID.
         */

        Order updatedOrder =
                orderRepository.findById(
                        order.getId()
                ).orElseThrow();

        assertThat(updatedOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.PAID
                );


        /*
         * Two successful payments should exist.
         */

        assertThat(
                paymentRepository
                        .findAllByInvoiceIdAndStatus(
                                invoice.getId(),
                                PaymentStatus.SUCCESS
                        )
        )
                .hasSize(2);
    }
}