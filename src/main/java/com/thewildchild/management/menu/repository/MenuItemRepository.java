package com.thewildchild.management.menu.repository;

import com.thewildchild.management.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    boolean existsByNameIgnoreCaseAndCategoryId(String name, UUID categoryId);
}