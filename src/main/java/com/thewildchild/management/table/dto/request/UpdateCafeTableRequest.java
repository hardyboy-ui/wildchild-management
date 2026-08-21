package com.thewildchild.management.table.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCafeTableRequest {

    @Size(
            max = 50,
            message = "Table number must not exceed 50 characters"
    )
    private String tableNumber;

    @Min(
            value = 1,
            message = "Capacity must be at least 1"
    )
    private Integer capacity;

    @Min(
            value = 1,
            message = "Display order must be at least 1"
    )
    private Integer displayOrder;
}