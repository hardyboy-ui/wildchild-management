package com.thewildchild.management.invoice.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.invoice.entity.DiscountType;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceValidatorTest {

    private final InvoiceValidator invoiceValidator =
            new InvoiceValidator();


    // =========================================================
    // validateOrderCanBeInvoiced()
    // =========================================================

    @Test
    void shouldAllowUnbilledOrder() {

        // Arrange
        Order order = new Order();

        order.setBillingStatus(
                OrderBillingStatus.UNBILLED
        );

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateOrderCanBeInvoiced(order)
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldRejectAlreadyBilledOrder() {

        // Arrange
        Order order = new Order();

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateOrderCanBeInvoiced(order)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Order has already been billed"
                );
    }


    @Test
    void shouldRejectPaidOrder() {

        // Arrange
        Order order = new Order();

        order.setBillingStatus(
                OrderBillingStatus.PAID
        );

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateOrderCanBeInvoiced(order)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Order has already been billed"
                );
    }


    // =========================================================
    // validateDiscount()
    // =========================================================

    @Test
    void shouldAllowNullDiscountValue() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        null,
                        null,
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldAllowZeroDiscountValue() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        null,
                        BigDecimal.ZERO,
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldRejectDiscountWithoutDiscountType() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateDiscount(
                        null,
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(200)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Discount type is required when discount value is provided"
                );
    }


    @Test
    void shouldRejectNegativeDiscount() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.FIXED,
                        BigDecimal.valueOf(-10),
                        BigDecimal.valueOf(200)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Discount value cannot be negative"
                );
    }


    @Test
    void shouldRejectPercentageDiscountAbove100() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.PERCENTAGE,
                        BigDecimal.valueOf(101),
                        BigDecimal.valueOf(200)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Percentage discount cannot exceed 100%"
                );
    }


    @Test
    void shouldAllowPercentageDiscountOf100() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.PERCENTAGE,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldAllowValidPercentageDiscount() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.PERCENTAGE,
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldRejectFixedDiscountAboveSubtotal() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.FIXED,
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(200)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Fixed discount cannot exceed subtotal"
                );
    }


    @Test
    void shouldAllowFixedDiscountEqualToSubtotal() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.FIXED,
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldAllowValidFixedDiscount() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateDiscount(
                        DiscountType.FIXED,
                        BigDecimal.valueOf(50),
                        BigDecimal.valueOf(200)
                )
        ).doesNotThrowAnyException();
    }


    // =========================================================
    // validateTax()
    // =========================================================

    @Test
    void shouldAllowNullTaxRate() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateTax(null)
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldAllowZeroTaxRate() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateTax(
                        BigDecimal.ZERO
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldRejectNegativeTaxRate() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateTax(
                        BigDecimal.valueOf(-5)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Tax rate cannot be negative"
                );
    }


    @Test
    void shouldRejectTaxRateAbove100() {

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceValidator.validateTax(
                        BigDecimal.valueOf(101)
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Tax rate cannot exceed 100%"
                );
    }


    @Test
    void shouldAllowTaxRateOf100() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateTax(
                        BigDecimal.valueOf(100)
                )
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldAllowValidTaxRate() {

        // Act & Assert
        assertThatCode(() ->
                invoiceValidator.validateTax(
                        BigDecimal.valueOf(18)
                )
        ).doesNotThrowAnyException();
    }
}