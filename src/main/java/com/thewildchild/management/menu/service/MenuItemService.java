package com.thewildchild.management.menu.service;

import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;

import java.util.List;
import java.util.UUID;

public interface MenuItemService {

    MenuItemResponse createMenuItem(CreateMenuItemRequest request);

    MenuItemResponse getMenuItemById(UUID itemId);

    List<MenuItemResponse> getAllMenuItems();

    MenuItemResponse updateMenuItem(
            UUID itemId,
            UpdateMenuItemRequest request
    );

    void deleteMenuItem(UUID itemId);
}