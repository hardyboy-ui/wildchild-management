package com.thewildchild.management.invoice.service.impl;

import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.entity.*;
import com.thewildchild.management.invoice.service.mapper.InvoiceMapper;
import com.thewildchild.management.invoice.repository.InvoiceOrderRepository;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.invoice.service.InvoiceService;
import com.thewildchild.management.invoice.service.validator.InvoiceValidator;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceOrderRepository invoiceOrderRepository;
    private final OrderRepository orderRepository;
    private final DiningSessionRepository diningSessionRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceValidator invoiceValidator;

    @Override
    public InvoiceResponse generateOrderInvoice(UUID orderId,
                                                GenerateInvoiceRequest request) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        invoiceValidator.validateOrderCanBeInvoiced(order);

        Invoice invoice = createInvoice(
                InvoiceBillingType.ORDER
        );

        addOrderToInvoice(invoice, order);

        BigDecimal subtotal =
                calculateSubtotal(List.of(order));

        invoice.setSubtotal(subtotal);

        invoiceValidator.validateDiscount(
                request.getDiscountType(),
                request.getDiscountValue(),
                subtotal
        );

        invoiceValidator.validateTax(
                request.getTaxRate()
        );

        calculateInvoiceTotals(
                invoice,
                request
        );

        order.setBillingStatus(OrderBillingStatus.BILLED);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse generateDiningSessionInvoice(
            UUID diningSessionId,GenerateInvoiceRequest request
    ) {

        DiningSession diningSession =
                diningSessionRepository
                        .findById(diningSessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dining session not found with id: "
                                                + diningSessionId
                                )
                        );

        List<Order> unbilledOrders =
                diningSession.getOrders()
                        .stream()
                        .filter(order ->
                                order.getBillingStatus()
                                        == OrderBillingStatus.UNBILLED
                        )
                        .toList();

        if (unbilledOrders.isEmpty()) {
            throw new BusinessException(
                    "No unbilled orders available for this dining session"
            );
        }

        Invoice invoice = createInvoice(
                InvoiceBillingType.DINING_SESSION
        );

        for (Order order : unbilledOrders) {

            invoiceValidator.validateOrderCanBeInvoiced(order);

            addOrderToInvoice(invoice, order);

            order.setBillingStatus(
                    OrderBillingStatus.BILLED
            );
        }

        BigDecimal subtotal =
                calculateSubtotal(unbilledOrders);

        invoice.setSubtotal(subtotal);

        invoiceValidator.validateDiscount(
                request.getDiscountType(),
                request.getDiscountValue(),
                subtotal
        );

        invoiceValidator.validateTax(
                request.getTaxRate()
        );

        calculateInvoiceTotals(
                invoice,
                request
        );

        Invoice savedInvoice =
                invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(
            UUID invoiceId
    ) {

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found with id: "
                                        + invoiceId
                        )
                );

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse cancelInvoice(
            UUID invoiceId
    ) {

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found with id: "
                                        + invoiceId
                        )
                );

        if (invoice.getStatus()
                != InvoiceStatus.GENERATED) {

            throw new BusinessException(
                    "Only unpaid generated invoices can be cancelled"
            );
        }

        for (InvoiceOrder invoiceOrder
                : invoice.getInvoiceOrders()) {

            Order order = invoiceOrder.getOrder();

            order.setBillingStatus(
                    OrderBillingStatus.UNBILLED
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);

        Invoice savedInvoice =
                invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    private Invoice createInvoice(
            InvoiceBillingType billingType
    ) {

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(
                generateInvoiceNumber()
        );

        invoice.setBillingType(billingType);

        invoice.setSubtotal(BigDecimal.ZERO);

        invoice.setDiscountValue(BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);

        invoice.setTaxRate(BigDecimal.ZERO);
        invoice.setTaxAmount(BigDecimal.ZERO);

        invoice.setGrandTotal(BigDecimal.ZERO);

        invoice.setStatus(
                InvoiceStatus.GENERATED
        );

        invoice.setInvoiceOrders(
                new ArrayList<>()
        );

        return invoice;
    }

    private void addOrderToInvoice(
            Invoice invoice,
            Order order
    ) {

        InvoiceOrder invoiceOrder =
                new InvoiceOrder();

        invoiceOrder.setInvoice(invoice);
        invoiceOrder.setOrder(order);

        invoice.getInvoiceOrders()
                .add(invoiceOrder);
    }

    private BigDecimal calculateSubtotal(
            List<Order> orders
    ) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (Order order : orders) {

            for (OrderItem orderItem : order.getItems()) {

                BigDecimal itemTotal =
                        orderItem.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                orderItem.getQuantity()
                                        )
                                );

                subtotal = subtotal.add(itemTotal);

                BigDecimal addOnTotal =
                        orderItem.getAddOns()
                                .stream()
                                .map(addOn ->
                                        addOn.getUnitPrice()
                                                .multiply(
                                                        BigDecimal.valueOf(
                                                                orderItem.getQuantity()
                                                        )
                                                )
                                )
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add
                                );

                subtotal = subtotal.add(addOnTotal);
            }
        }

        return subtotal;
    }

    private void calculateInvoiceTotals(
            Invoice invoice,
            GenerateInvoiceRequest request
    ) {

        BigDecimal subtotal = invoice.getSubtotal();

        BigDecimal discountAmount =
                calculateDiscountAmount(
                        subtotal,
                        request.getDiscountType(),
                        request.getDiscountValue()
                );

        BigDecimal taxableAmount =
                subtotal.subtract(discountAmount);

        BigDecimal taxRate =
                request.getTaxRate() != null
                        ? request.getTaxRate()
                        : BigDecimal.ZERO;

        BigDecimal taxAmount =
                taxableAmount
                        .multiply(taxRate)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal grandTotal =
                taxableAmount.add(taxAmount);

        invoice.setDiscountType(
                request.getDiscountType()
        );

        invoice.setDiscountValue(
                request.getDiscountValue() != null
                        ? request.getDiscountValue()
                        : BigDecimal.ZERO
        );

        invoice.setDiscountAmount(
                discountAmount
        );

        invoice.setTaxRate(taxRate);

        invoice.setTaxAmount(taxAmount);

        invoice.setGrandTotal(grandTotal);
    }

    private BigDecimal calculateDiscountAmount(
            BigDecimal subtotal,
            DiscountType discountType,
            BigDecimal discountValue
    ) {

        if (discountValue == null ||
                discountValue.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        if (discountType == DiscountType.FIXED) {

            return discountValue;
        }

        if (discountType == DiscountType.PERCENTAGE) {

            return subtotal
                    .multiply(discountValue)
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return BigDecimal.ZERO;
    }

    private String generateInvoiceNumber() {

        return "INV-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}