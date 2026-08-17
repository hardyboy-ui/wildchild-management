package com.thewildchild.management.menu.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MenuCategoryResponse {

    private UUID id;
    private String name;
    private String description;
    private Integer displayOrder;
}