package com.thewildchild.management.invoice.service.mapper;

import com.thewildchild.management.invoice.dto.response.InvoiceOrderResponse;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {

    public InvoiceResponse toResponse(Invoice entity) {

        InvoiceResponse response = new InvoiceResponse();

        response.setId(entity.getId());
        response.setInvoiceNumber(entity.getInvoiceNumber());
        response.setBillingType(entity.getBillingType());

        response.setSubtotal(entity.getSubtotal());
        response.setDiscount(entity.getDiscount());
        response.setTax(entity.getTax());
        response.setGrandTotal(entity.getGrandTotal());

        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        if (entity.getInvoiceOrders() != null) {
            response.setOrders(
                    entity.getInvoiceOrders()
                            .stream()
                            .map(this::toInvoiceOrderResponse)
                            .toList()
            );
        } else {
            response.setOrders(List.of());
        }

        return response;
    }

    private InvoiceOrderResponse toInvoiceOrderResponse(
            InvoiceOrder entity
    ) {

        InvoiceOrderResponse response =
                new InvoiceOrderResponse();

        response.setOrderId(
                entity.getOrder().getId()
        );

        response.setOrderNumber(
                entity.getOrder().getOrderNumber()
        );

        return response;
    }
}