package com.thewildchild.management.menu.dto.request;

import com.thewildchild.management.menu.entity.FoodType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateMenuItemRequest {

    @NotBlank(message = "Menu item name is required")
    @Size(max = 150, message = "Menu item name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "Food type is required")
    private FoodType foodType;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}