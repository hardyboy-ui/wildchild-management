package com.thewildchild.management.menu.repository;

import com.thewildchild.management.menu.entity.MenuItemAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuItemAddOnRepository
        extends JpaRepository<MenuItemAddOn, UUID> {

    boolean existsByMenuItemIdAndAddOnId(UUID menuItemId, UUID addOnId);
}