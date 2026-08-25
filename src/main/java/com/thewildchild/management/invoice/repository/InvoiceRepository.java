package com.thewildchild.management.invoice.repository;

import com.thewildchild.management.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository
        extends JpaRepository<Invoice, UUID> {
}