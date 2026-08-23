package com.thewildchild.management.order.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import com.thewildchild.management.menu.entity.AddOn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_item_add_ons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_item_add_on",
                        columnNames = {"order_item_id", "add_on_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OrderItemAddOn extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_item_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_order_item_add_on_order_item"
            )
    )
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "add_on_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_order_item_add_on_add_on"
            )
    )
    private AddOn addOn;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal unitPrice;
}