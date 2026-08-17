package com.thewildchild.management.menu.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "menu_item_add_ons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_menu_item_add_on",
                        columnNames = {"menu_item_id", "add_on_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MenuItemAddOn extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_add_on_item")
    )
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "add_on_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_item_add_on_add_on")
    )
    private AddOn addOn;
}