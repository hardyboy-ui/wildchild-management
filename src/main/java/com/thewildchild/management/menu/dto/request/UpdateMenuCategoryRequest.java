package com.thewildchild.management.menu.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMenuCategoryRequest {

    private String name;

    private String description;

    private Integer displayOrder;
}