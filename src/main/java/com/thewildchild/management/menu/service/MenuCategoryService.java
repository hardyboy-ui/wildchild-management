package com.thewildchild.management.menu.service;

import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface MenuCategoryService {

    MenuCategoryResponse createCategory(CreateMenuCategoryRequest request);

    MenuCategoryResponse getCategoryById(UUID categoryId);

    List<MenuCategoryResponse> getAllCategories();

    MenuCategoryResponse updateCategory(
            UUID categoryId,
            UpdateMenuCategoryRequest request
    );

    void deleteCategory(UUID categoryId);
}