package com.thewildchild.management.menu.service.mapper;

import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;
import com.thewildchild.management.menu.entity.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItem toEntity(CreateMenuItemRequest request) {
        MenuItem item = new MenuItem();

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setFoodType(request.getFoodType());

        return item;
    }

    public void updateEntity(
            MenuItem item,
            UpdateMenuItemRequest request
    ) {
        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }

        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }

        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        if (request.getFoodType() != null) {
            item.setFoodType(request.getFoodType());
        }
    }

    public MenuItemResponse toResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();

        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setImageUrl(item.getImageUrl());
        response.setFoodType(item.getFoodType());

        if (item.getCategory() != null) {
            response.setCategoryId(item.getCategory().getId());
            response.setCategoryName(item.getCategory().getName());
        }

        return response;
    }
}