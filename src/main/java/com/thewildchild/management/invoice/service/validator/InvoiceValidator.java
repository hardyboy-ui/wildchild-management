package com.thewildchild.management.invoice.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import org.springframework.stereotype.Component;

@Component
public class InvoiceValidator {
    public void validateOrderCanBeInvoiced(
            Order order
    ) {

        if (order.getBillingStatus()
                != OrderBillingStatus.UNBILLED) {

            throw new BusinessException(
                    "Order has already been billed"
            );
        }
    }
}
