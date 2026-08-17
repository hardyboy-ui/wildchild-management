package com.thewildchild.management.menu.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "add_ons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_add_on_name",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class AddOn extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}