package com.thewildchild.management.payment.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceOrder;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.dto.response.PaymentResponse;
import com.thewildchild.management.payment.entity.Payment;
import com.thewildchild.management.payment.entity.PaymentMethod;
import com.thewildchild.management.payment.entity.PaymentStatus;
import com.thewildchild.management.payment.repository.PaymentRepository;
import com.thewildchild.management.payment.service.mapper.PaymentMapper;
import com.thewildchild.management.payment.service.validator.PaymentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentValidator paymentValidator;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    // =========================================================
    // createPayment()
    // =========================================================

//    @Test
//    void shouldCreatePaymentSuccessfully() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.GENERATED
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(200),
//                        null
//                );
//
//        Payment savedPayment = new Payment();
//
//        savedPayment.setId(UUID.randomUUID());
//        savedPayment.setInvoice(invoice);
//        savedPayment.setPaymentMethod(
//                PaymentMethod.CASH
//        );
//        savedPayment.setAmount(
//                BigDecimal.valueOf(200)
//        );
//        savedPayment.setStatus(
//                PaymentStatus.SUCCESS
//        );
//
//        PaymentResponse response =
//                new PaymentResponse();
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        when(paymentRepository.save(any(Payment.class)))
//                .thenReturn(savedPayment);
//
//        when(paymentMapper.toResponse(savedPayment))
//                .thenReturn(response);
//
//        // Act
//        PaymentResponse result =
//                paymentService.createPayment(
//                        invoiceId,
//                        request
//                );
//
//        // Assert
//        assertThat(result)
//                .isSameAs(response);
//
//        assertThat(invoice.getStatus())
//                .isEqualTo(
//                        InvoiceStatus.PARTIALLY_PAID
//                );
//
//        verify(paymentValidator)
//                .validateInvoiceCanAcceptPayment(invoice);
//
//        verify(paymentValidator)
//                .validatePaymentRequest(request);
//
//        verify(paymentRepository)
//                .save(any(Payment.class));
//
//        verify(invoiceRepository)
//                .save(invoice);
//
//        verify(paymentMapper)
//                .toResponse(savedPayment);
//    }
//
//
//    @Test
//    void shouldMarkInvoiceAsPaidWhenFullAmountIsPaid() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.GENERATED
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(500),
//                        null
//                );
//
//        Payment savedPayment = new Payment();
//
//        savedPayment.setId(UUID.randomUUID());
//        savedPayment.setInvoice(invoice);
//        savedPayment.setAmount(
//                BigDecimal.valueOf(500)
//        );
//        savedPayment.setPaymentMethod(
//                PaymentMethod.CASH
//        );
//        savedPayment.setStatus(
//                PaymentStatus.SUCCESS
//        );
//
//        PaymentResponse response =
//                new PaymentResponse();
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        when(paymentRepository.save(any(Payment.class)))
//                .thenReturn(savedPayment);
//
//        when(paymentMapper.toResponse(savedPayment))
//                .thenReturn(response);
//
//        // Act
//        paymentService.createPayment(
//                invoiceId,
//                request
//        );
//
//        // Assert
//        assertThat(invoice.getStatus())
//                .isEqualTo(
//                        InvoiceStatus.PAID
//                );
//
//        verify(invoiceRepository)
//                .save(invoice);
//    }
//
//
//    @Test
//    void shouldMarkInvoiceOrdersAsPaidWhenInvoiceIsFullyPaid() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.GENERATED
//        );
//
//        Order order1 = createOrder();
//        Order order2 = createOrder();
//
//        InvoiceOrder invoiceOrder1 =
//                new InvoiceOrder();
//
//        invoiceOrder1.setInvoice(invoice);
//        invoiceOrder1.setOrder(order1);
//
//        InvoiceOrder invoiceOrder2 =
//                new InvoiceOrder();
//
//        invoiceOrder2.setInvoice(invoice);
//        invoiceOrder2.setOrder(order2);
//
//        invoice.setInvoiceOrders(
//                new ArrayList<>(
//                        List.of(
//                                invoiceOrder1,
//                                invoiceOrder2
//                        )
//                )
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(500),
//                        null
//                );
//
//        Payment savedPayment = createSuccessfulPayment(
//                invoice,
//                BigDecimal.valueOf(500)
//        );
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        when(paymentRepository.save(any(Payment.class)))
//                .thenReturn(savedPayment);
//
//        when(paymentMapper.toResponse(savedPayment))
//                .thenReturn(new PaymentResponse());
//
//        // Act
//        paymentService.createPayment(
//                invoiceId,
//                request
//        );
//
//        // Assert
//        assertThat(order1.getBillingStatus())
//                .isEqualTo(
//                        OrderBillingStatus.PAID
//                );
//
//        assertThat(order2.getBillingStatus())
//                .isEqualTo(
//                        OrderBillingStatus.PAID
//                );
//    }
//
//
//    @Test
//    void shouldAllowPaymentWhenPreviousSuccessfulPaymentExists() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.PARTIALLY_PAID
//        );
//
//        Payment previousPayment =
//                createSuccessfulPayment(
//                        invoice,
//                        BigDecimal.valueOf(200)
//                );
//
//        invoice.setPayments(
//                new ArrayList<>(
//                        List.of(previousPayment)
//                )
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(300),
//                        null
//                );
//
//        Payment newPayment =
//                createSuccessfulPayment(
//                        invoice,
//                        BigDecimal.valueOf(300)
//                );
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        when(paymentRepository.save(any(Payment.class)))
//                .thenReturn(newPayment);
//
//        when(paymentMapper.toResponse(newPayment))
//                .thenReturn(new PaymentResponse());
//
//        // Act
//        paymentService.createPayment(
//                invoiceId,
//                request
//        );
//
//        // Assert
//        assertThat(invoice.getStatus())
//                .isEqualTo(
//                        InvoiceStatus.PAID
//                );
//    }
//
//
//    @Test
//    void shouldIgnoreFailedPaymentsWhenCalculatingTotalPaid() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.GENERATED
//        );
//
//        Payment failedPayment =
//                new Payment();
//
//        failedPayment.setInvoice(invoice);
//        failedPayment.setAmount(
//                BigDecimal.valueOf(400)
//        );
//        failedPayment.setStatus(
//                PaymentStatus.FAILED
//        );
//
//        invoice.setPayments(
//                new ArrayList<>(
//                        List.of(failedPayment)
//                )
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(100),
//                        null
//                );
//
//        Payment savedPayment =
//                createSuccessfulPayment(
//                        invoice,
//                        BigDecimal.valueOf(100)
//                );
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        when(paymentRepository.save(any(Payment.class)))
//                .thenReturn(savedPayment);
//
//        when(paymentMapper.toResponse(savedPayment))
//                .thenReturn(new PaymentResponse());
//
//        // Act
//        paymentService.createPayment(
//                invoiceId,
//                request
//        );
//
//        // Assert
//        assertThat(invoice.getStatus())
//                .isEqualTo(
//                        InvoiceStatus.PARTIALLY_PAID
//                );
//    }
//
//
//    @Test
//    void shouldRejectPaymentExceedingRemainingAmount() {
//
//        // Arrange
//        UUID invoiceId = UUID.randomUUID();
//
//        Invoice invoice = createInvoice(
//                invoiceId,
//                BigDecimal.valueOf(500),
//                InvoiceStatus.GENERATED
//        );
//
//        Payment previousPayment =
//                createSuccessfulPayment(
//                        invoice,
//                        BigDecimal.valueOf(300)
//                );
//
//        invoice.setPayments(
//                new ArrayList<>(
//                        List.of(previousPayment)
//                )
//        );
//
//        CreatePaymentRequest request =
//                createPaymentRequest(
//                        PaymentMethod.CASH,
//                        BigDecimal.valueOf(250),
//                        null
//                );
//
//        when(invoiceRepository.findById(invoiceId))
//                .thenReturn(Optional.of(invoice));
//
//        // Act & Assert
//        assertThatThrownBy(() ->
//                paymentService.createPayment(
//                        invoiceId,
//                        request
//                )
//        )
//                .isInstanceOf(BusinessException.class)
//                .hasMessage(
//                        "Payment amount cannot exceed remaining invoice amount"
//                );
//
//        verify(paymentRepository, never())
//                .save(any());
//
//        verify(invoiceRepository, never())
//                .save(any());
//    }


    @Test
    void shouldThrowExceptionWhenInvoiceDoesNotExist() {

        // Arrange
        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                createPaymentRequest(
                        PaymentMethod.CASH,
                        BigDecimal.valueOf(100),
                        null
                );

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.empty());

        // Act & Assert
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
                        "Invoice not found with id: "
                                + invoiceId
                );

        verify(paymentValidator, never())
                .validateInvoiceCanAcceptPayment(any());

        verify(paymentRepository, never())
                .save(any());
    }


    // =========================================================
    // getPaymentById()
    // =========================================================

    @Test
    void shouldGetPaymentByIdSuccessfully() {

        // Arrange
        UUID paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentResponse response =
                new PaymentResponse();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(response);

        // Act
        PaymentResponse result =
                paymentService.getPaymentById(paymentId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        verify(paymentRepository)
                .findById(paymentId);

        verify(paymentMapper)
                .toResponse(payment);
    }


    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist() {

        // Arrange
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                paymentService.getPaymentById(paymentId)
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Payment not found with id: "
                                + paymentId
                );

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // Helper methods
    // =========================================================

    private Invoice createInvoice(
            UUID invoiceId,
            BigDecimal grandTotal,
            InvoiceStatus status
    ) {

        Invoice invoice = new Invoice();

        invoice.setId(invoiceId);
        invoice.setGrandTotal(grandTotal);
        invoice.setStatus(status);

        invoice.setPayments(
                new ArrayList<>()
        );

        invoice.setInvoiceOrders(
                new ArrayList<>()
        );

        return invoice;
    }


    private Order createOrder() {

        Order order = new Order();

        order.setId(UUID.randomUUID());

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        return order;
    }


    private Payment createSuccessfulPayment(
            Invoice invoice,
            BigDecimal amount
    ) {

        Payment payment = new Payment();

        payment.setId(UUID.randomUUID());
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        return payment;
    }


    private CreatePaymentRequest createPaymentRequest(
            PaymentMethod paymentMethod,
            BigDecimal amount,
            String transactionReference
    ) {

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(paymentMethod);
        request.setAmount(amount);
        request.setTransactionReference(
                transactionReference
        );

        return request;
    }
}