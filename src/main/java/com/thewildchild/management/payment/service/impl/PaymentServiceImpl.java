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
import com.thewildchild.management.payment.service.mapper.PaymentMapper;
import com.thewildchild.management.payment.repository.PaymentRepository;
import com.thewildchild.management.payment.service.PaymentService;
import com.thewildchild.management.payment.service.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentValidator paymentValidator;

    @Override
    public PaymentResponse createPayment(
            UUID invoiceId,
            CreatePaymentRequest request
    ) {

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found with id: " + invoiceId
                        )
                );

        paymentValidator.validateInvoiceCanAcceptPayment(invoice);
        paymentValidator.validatePaymentRequest(request);

        BigDecimal totalPaid = invoice.getPayments()
                .stream()
                .filter(payment ->
                        payment.getStatus() == PaymentStatus.SUCCESS
                )
                .map(Payment::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal remainingAmount =
                invoice.getGrandTotal()
                        .subtract(totalPaid);

        if (request.getAmount().compareTo(remainingAmount) > 0) {

            throw new BusinessException(
                    "Payment amount cannot exceed remaining invoice amount"
            );
        }

        Payment payment = new Payment();

        payment.setInvoice(invoice);
        payment.setPaymentMethod(
                request.getPaymentMethod()
        );
        payment.setAmount(
                request.getAmount()
        );
        payment.setTransactionReference(
                request.getTransactionReference()
        );

        /*
         * For V1, payment recorded by the manager
         * is considered successful immediately.
         */
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment =
                paymentRepository.save(payment);

        BigDecimal updatedTotalPaid =
                totalPaid.add(savedPayment.getAmount());

        if (updatedTotalPaid.compareTo(
                invoice.getGrandTotal()
        ) == 0) {

            invoice.setStatus(
                    InvoiceStatus.PAID
            );

            markInvoiceOrdersAsPaid(invoice);

        } else {

            invoice.setStatus(
                    InvoiceStatus.PARTIALLY_PAID
            );
        }

        invoiceRepository.save(invoice);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            java.util.UUID paymentId
    ) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: "
                                        + paymentId
                        )
                );

        return paymentMapper.toResponse(payment);
    }

    private void markInvoiceOrdersAsPaid(
            Invoice invoice
    ) {

        for (InvoiceOrder invoiceOrder
                : invoice.getInvoiceOrders()) {

            Order order = invoiceOrder.getOrder();

            order.setBillingStatus(
                    OrderBillingStatus.PAID
            );
        }
    }
}