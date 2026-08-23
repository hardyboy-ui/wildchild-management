package com.thewildchild.management.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddOrderItemAddOnRequest {

    @NotNull(message = "Add-on ID is required")
    private UUID addOnId;
}