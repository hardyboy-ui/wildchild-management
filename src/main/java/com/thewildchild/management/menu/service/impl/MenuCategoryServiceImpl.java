package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.service.MenuCategoryService;
import com.thewildchild.management.menu.service.mapper.MenuCategoryMapper;
import com.thewildchild.management.menu.service.validator.MenuCategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuCategoryMapper menuCategoryMapper;
    private final MenuCategoryValidator menuCategoryValidator;

    @Override
    public MenuCategoryResponse createCategory(
            CreateMenuCategoryRequest request
    ) {
        menuCategoryValidator.validateCreate(request);

        MenuCategory category = menuCategoryMapper.toEntity(request);

        MenuCategory savedCategory =
                menuCategoryRepository.save(category);

        return menuCategoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuCategoryResponse getCategoryById(UUID categoryId) {

        MenuCategory category = findCategoryById(categoryId);

        return menuCategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getAllCategories() {

        return menuCategoryRepository.findAllByActiveTrue()
                .stream()
                .map(menuCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public MenuCategoryResponse updateCategory(
            UUID categoryId,
            UpdateMenuCategoryRequest request
    ) {
        MenuCategory category = findCategoryById(categoryId);

        menuCategoryValidator.validateUpdate(category, request);

        menuCategoryMapper.updateEntity(category, request);

        MenuCategory updatedCategory =
                menuCategoryRepository.save(category);

        return menuCategoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {

        MenuCategory category = findCategoryById(categoryId);
        category.setActive(false);
    }

    private MenuCategory findCategoryById(UUID categoryId) {

        return menuCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu category not found with id: " + categoryId
                        )
                );
    }
}