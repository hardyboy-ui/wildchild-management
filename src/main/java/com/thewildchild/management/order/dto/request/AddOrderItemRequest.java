package com.thewildchild.management.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AddOrderItemRequest {

    @NotNull(message = "Menu item ID is required")
    private UUID menuItemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(
            max = 500,
            message = "Special instructions cannot exceed 500 characters"
    )
    private String specialInstructions;

    @Valid
    private List<AddOrderItemAddOnRequest> addOns;
}