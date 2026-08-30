package com.thewildchild.management.payment.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PaymentValidatorTest {

    @InjectMocks
    private PaymentValidator paymentValidator;


    // =========================================================
    // validateInvoiceCanAcceptPayment()
    // =========================================================

    @Test
    void shouldAllowPaymentWhenInvoiceIsPartiallyPaid() {

        // Arrange
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);

        // Act & Assert
        assertThatCode(() ->
                paymentValidator.validateInvoiceCanAcceptPayment(invoice)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectPaymentWhenInvoiceIsAlreadyPaid() {

        // Arrange
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.PAID);

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validateInvoiceCanAcceptPayment(invoice)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invoice is already fully paid");
    }

    @Test
    void shouldRejectPaymentWhenInvoiceIsCancelled() {

        // Arrange
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.CANCELLED);

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validateInvoiceCanAcceptPayment(invoice)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cancelled invoice cannot accept payment");
    }


    // =========================================================
    // validatePaymentRequest()
    // =========================================================

    @Test
    void shouldAllowValidCashPaymentRequest() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(BigDecimal.valueOf(500));

        // Act & Assert
        assertThatCode(() ->
                paymentValidator.validatePaymentRequest(request)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldAllowValidQrPaymentRequest() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.QR);
        request.setAmount(BigDecimal.valueOf(500));
        request.setTransactionReference("TXN-12345");

        // Act & Assert
        assertThatCode(() ->
                paymentValidator.validatePaymentRequest(request)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectPaymentWhenPaymentMethodIsNull() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setAmount(BigDecimal.valueOf(500));

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment method is required");
    }

    @Test
    void shouldRejectPaymentWhenAmountIsNull() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment amount must be greater than zero");
    }

    @Test
    void shouldRejectPaymentWhenAmountIsZero() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment amount must be greater than zero");
    }

    @Test
    void shouldRejectPaymentWhenAmountIsNegative() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(BigDecimal.valueOf(-100));

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment amount must be greater than zero");
    }

    @Test
    void shouldRejectQrPaymentWhenTransactionReferenceIsNull() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.QR);
        request.setAmount(BigDecimal.valueOf(500));

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Transaction reference is required for QR payment"
                );
    }

    @Test
    void shouldRejectQrPaymentWhenTransactionReferenceIsBlank() {

        // Arrange
        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.QR);
        request.setAmount(BigDecimal.valueOf(500));
        request.setTransactionReference("   ");

        // Act & Assert
        assertThatThrownBy(() ->
                paymentValidator.validatePaymentRequest(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Transaction reference is required for QR payment"
                );
    }
}