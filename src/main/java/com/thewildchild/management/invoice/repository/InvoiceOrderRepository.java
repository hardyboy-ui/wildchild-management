package com.thewildchild.management.invoice.repository;

import com.thewildchild.management.invoice.entity.InvoiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceOrderRepository
        extends JpaRepository<InvoiceOrder, UUID> {
}