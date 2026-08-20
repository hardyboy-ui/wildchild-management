package com.thewildchild.management.menu.repository;

import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddOnRepository extends JpaRepository<AddOn, UUID> {

    boolean existsByNameIgnoreCase(String name);

    List<AddOn> findAllByActiveTrue();

}