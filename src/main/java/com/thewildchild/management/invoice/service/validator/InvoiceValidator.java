package com.thewildchild.management.invoice.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.invoice.entity.DiscountType;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvoiceValidator {
    public void validateOrderCanBeInvoiced(
            Order order
    ) {

        if (order.getBillingStatus()
                != OrderBillingStatus.UNBILLED) {

            throw new BusinessException(
                    "Order has already been billed"
            );
        }
    }
    public void validateDiscount(
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal subtotal
    ) {

        if (discountValue == null ||
                discountValue.compareTo(BigDecimal.ZERO) == 0) {

            return;
        }

        if (discountType == null) {

            throw new BusinessException(
                    "Discount type is required when discount value is provided"
            );
        }

        if (discountValue.compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessException(
                    "Discount value cannot be negative"
            );
        }

        if (discountType == DiscountType.PERCENTAGE &&
                discountValue.compareTo(
                        BigDecimal.valueOf(100)
                ) > 0) {

            throw new BusinessException(
                    "Percentage discount cannot exceed 100%"
            );
        }

        if (discountType == DiscountType.FIXED &&
                discountValue.compareTo(subtotal) > 0) {

            throw new BusinessException(
                    "Fixed discount cannot exceed subtotal"
            );
        }
    }

    public void validateTax(
            BigDecimal taxRate
    ) {

        if (taxRate == null ||
                taxRate.compareTo(BigDecimal.ZERO) == 0) {

            return;
        }

        if (taxRate.compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessException(
                    "Tax rate cannot be negative"
            );
        }

        if (taxRate.compareTo(
                BigDecimal.valueOf(100)
        ) > 0) {

            throw new BusinessException(
                    "Tax rate cannot exceed 100%"
            );
        }
    }
}
