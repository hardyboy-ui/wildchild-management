package com.thewildchild.management.menu.repository;

import com.thewildchild.management.menu.entity.AddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddOnRepository extends JpaRepository<AddOn, UUID> {

    boolean existsByNameIgnoreCase(String name);
}