package com.thewildchild.management.invoice.dto.request;

import com.thewildchild.management.invoice.entity.DiscountType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GenerateInvoiceRequest {

    private DiscountType discountType;

    @DecimalMin(
            value = "0.00",
            message = "Discount value cannot be negative"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "Discount value must have at most 2 decimal places"
    )
    private BigDecimal discountValue;

    @DecimalMin(
            value = "0.00",
            message = "Tax rate cannot be negative"
    )
    @DecimalMax(
            value = "100.00",
            message = "Tax rate cannot exceed 100"
    )
    @Digits(
            integer = 3,
            fraction = 2,
            message = "Tax rate must have at most 2 decimal places"
    )
    private BigDecimal taxRate;
}