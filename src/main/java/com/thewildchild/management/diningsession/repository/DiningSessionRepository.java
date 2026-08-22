package com.thewildchild.management.diningsession.repository;

import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiningSessionRepository
        extends JpaRepository<DiningSession, UUID> {

    boolean existsByTableIdAndStatus(
            UUID tableId,
            DiningSessionStatus status
    );

    Optional<DiningSession> findByTableIdAndStatus(
            UUID tableId,
            DiningSessionStatus status
    );

    List<DiningSession> findAllByStatus(
            DiningSessionStatus status
    );
}