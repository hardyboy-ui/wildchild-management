package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuItemValidator {

    private final MenuItemRepository menuItemRepository;

    public void validateCreate(CreateMenuItemRequest request) {

        if (menuItemRepository.existsByNameIgnoreCaseAndCategoryId(
                request.getName(),
                request.getCategoryId()
        )) {
            throw new BusinessException(
                    "Menu item with name '" + request.getName()
                            + "' already exists in this category"
            );
        }
    }

    public void validateUpdate(
            MenuItem item,
            UpdateMenuItemRequest request
    ) {

        String newName = request.getName() != null
                ? request.getName()
                : item.getName();

        var newCategoryId = request.getCategoryId() != null
                ? request.getCategoryId()
                : item.getCategory().getId();

        boolean nameChanged =
                !newName.equalsIgnoreCase(item.getName());

        boolean categoryChanged =
                !newCategoryId.equals(item.getCategory().getId());

        if ((nameChanged || categoryChanged)
                && menuItemRepository.existsByNameIgnoreCaseAndCategoryId(
                newName,
                newCategoryId
        )) {

            throw new BusinessException(
                    "Menu item with name '" + newName
                            + "' already exists in this category"
            );
        }
    }
}

