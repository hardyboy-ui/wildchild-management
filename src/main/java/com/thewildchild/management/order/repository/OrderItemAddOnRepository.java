package com.thewildchild.management.order.repository;

import com.thewildchild.management.order.entity.OrderItemAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemAddOnRepository
        extends JpaRepository<OrderItemAddOn, UUID> {

    List<OrderItemAddOn> findAllByOrderItemId(UUID orderItemId);

    boolean existsByOrderItemIdAndAddOnId(
            UUID orderItemId,
            UUID addOnId
    );
}