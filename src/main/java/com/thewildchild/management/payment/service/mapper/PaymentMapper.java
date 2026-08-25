package com.thewildchild.management.payment.service.mapper;

import com.thewildchild.management.payment.dto.response.PaymentResponse;
import com.thewildchild.management.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment entity) {

        PaymentResponse response = new PaymentResponse();

        response.setId(entity.getId());

        response.setInvoiceId(
                entity.getInvoice().getId()
        );

        response.setPaymentMethod(
                entity.getPaymentMethod()
        );

        response.setAmount(
                entity.getAmount()
        );

        response.setStatus(
                entity.getStatus()
        );

        response.setTransactionReference(
                entity.getTransactionReference()
        );

        response.setCreatedAt(
                entity.getCreatedAt()
        );

        return response;
    }
}