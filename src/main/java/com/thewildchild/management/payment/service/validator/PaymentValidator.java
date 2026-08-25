package com.thewildchild.management.payment.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.entity.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator {

    public void validateInvoiceCanAcceptPayment(
            Invoice invoice
    ) {
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException(
                    "Invoice is already fully paid"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException(
                    "Cancelled invoice cannot accept payment"
            );
        }
    }

    public void validatePaymentRequest(
            CreatePaymentRequest request
    ) {

        if (request.getPaymentMethod() == null) {

            throw new BusinessException(
                    "Payment method is required"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new BusinessException(
                    "Payment amount must be greater than zero"
            );
        }

        if (request.getPaymentMethod()
                == PaymentMethod.QR
                && (request.getTransactionReference() == null
                || request.getTransactionReference().isBlank())) {

            throw new BusinessException(
                    "Transaction reference is required for QR payment"
            );
        }
    }
}
