package com.thewildchild.management.menu.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMenuCategoryRequest {

    @NotBlank
    private String name;

    private String description;

    private Integer displayOrder;
}