package com.thewildchild.management.invoice.entity;

import com.thewildchild.management.common.entity.BaseEntity;
import com.thewildchild.management.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "invoice_orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoice_order",
                        columnNames = {"invoice_id", "order_id"}
                )
        }
)
@Getter
@Setter
public class InvoiceOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "invoice_id",
            nullable = false
    )
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;
}