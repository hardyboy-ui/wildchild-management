package com.thewildchild.management.menu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateAddOnRequest {

    @NotBlank(message = "Add-on name is required")
    @Size(max = 100, message = "Add-on name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be ne" +
            "gative")
    private BigDecimal price;
}