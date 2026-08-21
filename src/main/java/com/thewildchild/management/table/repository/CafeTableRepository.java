package com.thewildchild.management.table.repository;

import com.thewildchild.management.table.entity.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CafeTableRepository extends JpaRepository<CafeTable, UUID> {

    boolean existsByTableNumberIgnoreCase(String tableNumber);

    List<CafeTable> findAllByActiveTrue();
}