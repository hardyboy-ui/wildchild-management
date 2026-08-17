package com.thewildchild.management.menu.service.mapper;

import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;
import com.thewildchild.management.menu.entity.MenuCategory;
import org.springframework.stereotype.Component;

@Component
public class MenuCategoryMapper {

    public MenuCategory toEntity(CreateMenuCategoryRequest request) {
        MenuCategory category = new MenuCategory();

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());

        return category;
    }

    public void updateEntity(
            MenuCategory category,
            UpdateMenuCategoryRequest request
    ) {
        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public MenuCategoryResponse toResponse(MenuCategory category) {
        MenuCategoryResponse response = new MenuCategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setDisplayOrder(category.getDisplayOrder());

        return response;
    }
}