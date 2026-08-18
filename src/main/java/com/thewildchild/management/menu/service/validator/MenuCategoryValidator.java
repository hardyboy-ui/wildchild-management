package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuCategoryValidator {

    private final MenuCategoryRepository menuCategoryRepository;

    public void validateCreate(CreateMenuCategoryRequest request) {
        if (menuCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException(
                    "Menu category with name '" + request.getName() + "' already exists"
            );
        }
    }

    public void validateUpdate(
            MenuCategory category,
            UpdateMenuCategoryRequest request
    ) {
        if (request.getName() != null
                && !request.getName().isBlank()
                && !request.getName().equalsIgnoreCase(category.getName())
                && menuCategoryRepository.existsByNameIgnoreCase(request.getName())) {

            throw new BusinessException(
                    "Menu category with name '" + request.getName() + "' already exists"
            );
        }
    }
}