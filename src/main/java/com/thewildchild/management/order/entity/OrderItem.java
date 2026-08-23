package com.thewildchild.management.order.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import com.thewildchild.management.menu.entity.MenuItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_order_item_order"
            )
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_item_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_order_item_menu_item"
            )
    )
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(
            name = "kot_sent_quantity",
            nullable = false
    )
    private Integer kotSentQuantity = 0;

    @Column(
            name = "special_instructions",
            length = 500
    )
    private String specialInstructions;

    @OneToMany(
            mappedBy = "orderItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemAddOn> addOns = new ArrayList<>();
}