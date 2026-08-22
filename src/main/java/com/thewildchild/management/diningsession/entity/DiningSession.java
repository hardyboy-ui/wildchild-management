package com.thewildchild.management.diningsession.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import com.thewildchild.management.table.entity.CafeTable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dining_sessions")
@Getter
@Setter
@NoArgsConstructor
public class DiningSession extends BaseEntity {

    @Column(
            name = "session_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String sessionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "table_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dining_session_table")
    )
    private CafeTable table;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiningSessionStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}