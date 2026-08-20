package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.entity.MenuItemAddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.menu.service.MenuItemAddOnService;
import com.thewildchild.management.menu.service.mapper.AddOnMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemAddOnServiceImpl implements MenuItemAddOnService {

    private final MenuItemRepository menuItemRepository;
    private final AddOnRepository addOnRepository;
    private final MenuItemAddOnRepository menuItemAddOnRepository;
    private final AddOnMapper addOnMapper;

    @Override
    public void addAddOnToMenuItem(UUID menuItemId, UUID addOnId) {

        MenuItem menuItem = findActiveMenuItem(menuItemId);
        AddOn addOn = findActiveAddOn(addOnId);

        if (menuItemAddOnRepository.existsByMenuItemIdAndAddOnId(
                menuItemId, addOnId)) {
            throw new BusinessException(
                    "Add-on is already attached to this menu item"
            );
        }

        MenuItemAddOn mapping = new MenuItemAddOn();
        mapping.setMenuItem(menuItem);
        mapping.setAddOn(addOn);

        menuItemAddOnRepository.save(mapping);
    }

    @Override
    public void removeAddOnFromMenuItem(
            UUID menuItemId,
            UUID addOnId
    ) {

        MenuItemAddOn mapping = menuItemAddOnRepository
                .findByMenuItemIdAndAddOnId(menuItemId, addOnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Add-on is not attached to this menu item"
                        )
                );

        menuItemAddOnRepository.delete(mapping);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddOnResponse> getAddOnsForMenuItem(
            UUID menuItemId
    ) {

        findActiveMenuItem(menuItemId);

        return menuItemAddOnRepository
                .findAllByMenuItemIdAndAddOnActiveTrue(menuItemId)
                .stream()
                .map(MenuItemAddOn::getAddOn)
                .map(addOnMapper::toResponse)
                .toList();
    }

    private MenuItem findActiveMenuItem(UUID menuItemId) {

        return menuItemRepository.findById(menuItemId)
                .filter(MenuItem::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found with id: " + menuItemId
                        )
                );
    }

    private AddOn findActiveAddOn(UUID addOnId) {

        return addOnRepository.findById(addOnId)
                .filter(AddOn::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Add-on not found with id: " + addOnId
                        )
                );
    }
}