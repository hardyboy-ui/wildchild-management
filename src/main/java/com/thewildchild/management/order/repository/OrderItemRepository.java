package com.thewildchild.management.order.repository;

import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.report.dto.projection.CategorySalesProjection;
import com.thewildchild.management.report.dto.projection.MenuItemSalesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByOrderId(UUID orderId);

    @Query("""
        SELECT
            oi.menuItem.id AS menuItemId,
            oi.menuItem.name AS menuItemName,
            SUM(oi.quantity) AS quantitySold,
            COALESCE(
                SUM(
                    oi.unitPrice * oi.quantity
                ),
                0
            ) AS revenue

        FROM OrderItem oi

        JOIN oi.order o
        JOIN InvoiceOrder io
            ON io.order = o
        JOIN io.invoice i

        WHERE i.status =
              com.thewildchild.management.invoice.entity.InvoiceStatus.PAID

          AND i.createdAt >= :start
          AND i.createdAt < :end

        GROUP BY
            oi.menuItem.id,
            oi.menuItem.name

        ORDER BY
            revenue DESC
        """)
    List<MenuItemSalesProjection> getTopSellingMenuItems(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT
            oi.menuItem.category.id AS categoryId,
            oi.menuItem.category.name AS categoryName,
            SUM(oi.quantity) AS quantitySold,
            COALESCE(
                SUM(
                    oi.unitPrice * oi.quantity
                ),
                0
            ) AS revenue

        FROM OrderItem oi

        JOIN oi.order o
        JOIN InvoiceOrder io
            ON io.order = o
        JOIN io.invoice i

        WHERE i.status =
              com.thewildchild.management.invoice.entity.InvoiceStatus.PAID

          AND i.createdAt >= :start
          AND i.createdAt < :end

        GROUP BY
            oi.menuItem.category.id,
            oi.menuItem.category.name

        ORDER BY
            revenue DESC
        """)
    List<CategorySalesProjection> getCategorySales(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}