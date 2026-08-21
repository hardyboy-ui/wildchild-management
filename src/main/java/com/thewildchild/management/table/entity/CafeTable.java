package com.thewildchild.management.table.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "cafe_tables",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cafe_table_number",
                        columnNames = "table_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CafeTable extends BaseEntity {

    @Column(name = "table_number", nullable = false, length = 50)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}