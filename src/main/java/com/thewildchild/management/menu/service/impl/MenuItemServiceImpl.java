package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.menu.service.MenuItemService;
import com.thewildchild.management.menu.service.mapper.MenuItemMapper;
import com.thewildchild.management.menu.service.validator.MenuItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemMapper menuItemMapper;
    private final MenuItemValidator menuItemValidator;

    @Override
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {

        MenuCategory category = findActiveCategory(request.getCategoryId());

        menuItemValidator.validateCreate(request);

        MenuItem item = menuItemMapper.toEntity(request);
        item.setCategory(category);

        MenuItem savedItem = menuItemRepository.save(item);

        return menuItemMapper.toResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(UUID itemId) {

        MenuItem item = findActiveMenuItem(itemId);

        return menuItemMapper.toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItems() {

        return menuItemRepository.findAllByActiveTrue()
                .stream()
                .map(menuItemMapper::toResponse)
                .toList();
    }

    @Override
    public MenuItemResponse updateMenuItem(
            UUID itemId,
            UpdateMenuItemRequest request
    ) {

        MenuItem item = findActiveMenuItem(itemId);

        menuItemValidator.validateUpdate(item, request);

        menuItemMapper.updateEntity(item, request);

        if (request.getCategoryId() != null) {
            MenuCategory category =
                    findActiveCategory(request.getCategoryId());

            item.setCategory(category);
        }

        return menuItemMapper.toResponse(item);
    }

    @Override
    public void deleteMenuItem(UUID itemId) {

        MenuItem item = findActiveMenuItem(itemId);

        item.setActive(false);
    }

    private MenuItem findActiveMenuItem(UUID itemId) {

        return menuItemRepository.findById(itemId)
                .filter(MenuItem::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found with id: " + itemId
                        )
                );
    }

    private MenuCategory findActiveCategory(UUID categoryId) {

        return menuCategoryRepository.findById(categoryId)
                .filter(MenuCategory::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active menu category not found with id: " + categoryId
                        )
                );
    }
}