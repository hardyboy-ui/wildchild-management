package com.thewildchild.management.menu.dto.response;

import com.thewildchild.management.menu.entity.FoodType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class MenuItemResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private FoodType foodType;
    private UUID categoryId;
    private String categoryName;
}