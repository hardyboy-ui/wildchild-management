package com.thewildchild.management.invoice.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.entity.*;
import com.thewildchild.management.invoice.repository.InvoiceOrderRepository;
import com.thewildchild.management.invoice.repository.InvoiceRepository;
import com.thewildchild.management.invoice.service.mapper.InvoiceMapper;
import com.thewildchild.management.invoice.service.validator.InvoiceValidator;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceOrderRepository invoiceOrderRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private InvoiceValidator invoiceValidator;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;


    // =========================================================
    // generateOrderInvoice()
    // =========================================================

    @Test
    void shouldGenerateOrderInvoiceSuccessfully() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = createOrder(orderId);

        OrderItem item = createOrderItem(
                order,
                2,
                BigDecimal.valueOf(100)
        );

        order.getItems().add(item);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);

        InvoiceResponse response =
                new InvoiceResponse();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(invoiceMapper.toResponse(any(Invoice.class)))
                .thenReturn(response);

        // Act
        InvoiceResponse result =
                invoiceService.generateOrderInvoice(
                        orderId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        Invoice savedInvoice =
                captureSavedInvoice();

        assertThat(savedInvoice.getBillingType())
                .isEqualTo(InvoiceBillingType.ORDER);

        assertThat(savedInvoice.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(savedInvoice.getDiscountAmount())
                .isEqualByComparingTo("0.00");

        assertThat(savedInvoice.getTaxAmount())
                .isEqualByComparingTo("0.00");

        assertThat(savedInvoice.getGrandTotal())
                .isEqualByComparingTo("200.00");

        assertThat(order.getBillingStatus())
                .isEqualTo(OrderBillingStatus.BILLED);

        verify(invoiceValidator)
                .validateOrderCanBeInvoiced(order);

        verify(invoiceValidator)
                .validateDiscount(
                        request.getDiscountType(),
                        request.getDiscountValue(),
                        BigDecimal.valueOf(200)
                );

        verify(invoiceValidator)
                .validateTax(request.getTaxRate());

        verify(invoiceRepository)
                .save(any(Invoice.class));

        verify(invoiceMapper)
                .toResponse(any(Invoice.class));
    }


    @Test
    void shouldCalculateSubtotalIncludingAddOns() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = createOrder(orderId);

        OrderItem item =
                createOrderItem(
                        order,
                        2,
                        BigDecimal.valueOf(100)
                );

        AddOn addOn = new AddOn();
        addOn.setId(UUID.randomUUID());
        addOn.setName("Extra Cheese");

        com.thewildchild.management.order.entity.OrderItemAddOn
                itemAddOn =
                new com.thewildchild.management.order.entity.OrderItemAddOn();

        itemAddOn.setAddOn(addOn);
        itemAddOn.setUnitPrice(
                BigDecimal.valueOf(25)
        );

        item.getAddOns().add(itemAddOn);
        order.getItems().add(item);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(invoiceMapper.toResponse(any(Invoice.class)))
                .thenReturn(new InvoiceResponse());

        // Act
        invoiceService.generateOrderInvoice(
                orderId,
                request
        );

        // Assert
        Invoice savedInvoice =
                captureSavedInvoice();

        /*
         * Menu item:
         * 100 × 2 = 200
         *
         * Add-on:
         * 25 × 2 = 50
         *
         * Subtotal:
         * 200 + 50 = 250
         */

        assertThat(savedInvoice.getSubtotal())
                .isEqualByComparingTo("250.00");

        assertThat(savedInvoice.getGrandTotal())
                .isEqualByComparingTo("250.00");
    }


    @Test
    void shouldCalculatePercentageDiscountAndTax() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = createOrder(orderId);

        OrderItem item =
                createOrderItem(
                        order,
                        2,
                        BigDecimal.valueOf(100)
                );

        order.getItems().add(item);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(
                DiscountType.PERCENTAGE
        );

        request.setDiscountValue(
                BigDecimal.valueOf(10)
        );

        request.setTaxRate(
                BigDecimal.valueOf(18)
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(invoiceMapper.toResponse(any(Invoice.class)))
                .thenReturn(new InvoiceResponse());

        // Act
        invoiceService.generateOrderInvoice(
                orderId,
                request
        );

        // Assert
        Invoice savedInvoice =
                captureSavedInvoice();

        /*
         * Subtotal = 200
         *
         * 10% discount = 20
         *
         * Taxable amount = 180
         *
         * 18% tax = 32.40
         *
         * Grand total = 212.40
         */

        assertThat(savedInvoice.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(savedInvoice.getDiscountAmount())
                .isEqualByComparingTo("20.00");

        assertThat(savedInvoice.getTaxAmount())
                .isEqualByComparingTo("32.40");

        assertThat(savedInvoice.getGrandTotal())
                .isEqualByComparingTo("212.40");
    }


    @Test
    void shouldCalculateFixedDiscount() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        Order order = createOrder(orderId);

        OrderItem item =
                createOrderItem(
                        order,
                        2,
                        BigDecimal.valueOf(100)
                );

        order.getItems().add(item);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(
                DiscountType.FIXED
        );

        request.setDiscountValue(
                BigDecimal.valueOf(30)
        );

        request.setTaxRate(BigDecimal.ZERO);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(invoiceMapper.toResponse(any(Invoice.class)))
                .thenReturn(new InvoiceResponse());

        // Act
        invoiceService.generateOrderInvoice(
                orderId,
                request
        );

        // Assert
        Invoice savedInvoice =
                captureSavedInvoice();

        assertThat(savedInvoice.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(savedInvoice.getDiscountAmount())
                .isEqualByComparingTo("30.00");

        assertThat(savedInvoice.getGrandTotal())
                .isEqualByComparingTo("170.00");
    }


    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        // Arrange
        UUID orderId = UUID.randomUUID();

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceService.generateOrderInvoice(
                        orderId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Order not found with id: " + orderId
                );

        verify(invoiceRepository, never())
                .save(any());
    }


    // =========================================================
    // generateDiningSessionInvoice()
    // =========================================================

    @Test
    void shouldGenerateDiningSessionInvoiceSuccessfully() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setId(sessionId);
        session.setOrders(new ArrayList<>());

        Order order =
                createOrder(UUID.randomUUID());

        OrderItem item =
                createOrderItem(
                        order,
                        2,
                        BigDecimal.valueOf(100)
                );

        order.getItems().add(item);
        order.setBillingStatus(
                OrderBillingStatus.UNBILLED
        );

        session.getOrders().add(order);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(BigDecimal.ZERO);

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(invoiceMapper.toResponse(any(Invoice.class)))
                .thenReturn(new InvoiceResponse());

        // Act
        invoiceService.generateDiningSessionInvoice(
                sessionId,
                request
        );

        // Assert
        Invoice savedInvoice =
                captureSavedInvoice();

        assertThat(savedInvoice.getBillingType())
                .isEqualTo(
                        InvoiceBillingType.DINING_SESSION
                );

        assertThat(savedInvoice.getSubtotal())
                .isEqualByComparingTo("200.00");

        assertThat(savedInvoice.getGrandTotal())
                .isEqualByComparingTo("200.00");

        assertThat(order.getBillingStatus())
                .isEqualTo(OrderBillingStatus.BILLED);
    }


    @Test
    void shouldThrowExceptionWhenDiningSessionDoesNotExist() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceService.generateDiningSessionInvoice(
                        sessionId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Dining session not found with id: "
                                + sessionId
                );
    }


    @Test
    void shouldThrowExceptionWhenNoUnbilledOrdersExist() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setId(sessionId);
        session.setOrders(new ArrayList<>());

        Order order =
                createOrder(UUID.randomUUID());

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        session.getOrders().add(order);

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceService.generateDiningSessionInvoice(
                        sessionId,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "No unbilled orders available for this dining session"
                );
    }


    // =========================================================
    // getInvoiceById()
    // =========================================================

    @Test
    void shouldGetInvoiceByIdSuccessfully() {

        // Arrange
        UUID invoiceId = UUID.randomUUID();

        Invoice invoice =
                new Invoice();

        invoice.setId(invoiceId);

        InvoiceResponse response =
                new InvoiceResponse();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.of(invoice));

        when(invoiceMapper.toResponse(invoice))
                .thenReturn(response);

        // Act
        InvoiceResponse result =
                invoiceService.getInvoiceById(invoiceId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        verify(invoiceRepository)
                .findById(invoiceId);

        verify(invoiceMapper)
                .toResponse(invoice);
    }


    @Test
    void shouldThrowExceptionWhenInvoiceDoesNotExist() {

        // Arrange
        UUID invoiceId = UUID.randomUUID();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceService.getInvoiceById(invoiceId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Invoice not found with id: "
                                + invoiceId
                );
    }


    // =========================================================
    // cancelInvoice()
    // =========================================================

    @Test
    void shouldCancelInvoiceSuccessfully() {

        // Arrange
        UUID invoiceId = UUID.randomUUID();

        Invoice invoice =
                new Invoice();

        invoice.setId(invoiceId);
        invoice.setStatus(
                InvoiceStatus.GENERATED
        );

        Order order =
                createOrder(UUID.randomUUID());

        order.setBillingStatus(
                OrderBillingStatus.BILLED
        );

        InvoiceOrder invoiceOrder =
                new InvoiceOrder();

        invoiceOrder.setInvoice(invoice);
        invoiceOrder.setOrder(order);

        invoice.setInvoiceOrders(
                new ArrayList<>(
                        List.of(invoiceOrder)
                )
        );

        InvoiceResponse response =
                new InvoiceResponse();

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.of(invoice));

        when(invoiceRepository.save(invoice))
                .thenReturn(invoice);

        when(invoiceMapper.toResponse(invoice))
                .thenReturn(response);

        // Act
        InvoiceResponse result =
                invoiceService.cancelInvoice(invoiceId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(invoice.getStatus())
                .isEqualTo(
                        InvoiceStatus.CANCELLED
                );

        assertThat(order.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.UNBILLED
                );

        verify(invoiceRepository)
                .save(invoice);
    }


    @Test
    void shouldThrowExceptionWhenCancellingNonGeneratedInvoice() {

        // Arrange
        UUID invoiceId = UUID.randomUUID();

        Invoice invoice =
                new Invoice();

        invoice.setId(invoiceId);
        invoice.setStatus(
                InvoiceStatus.CANCELLED
        );

        when(invoiceRepository.findById(invoiceId))
                .thenReturn(Optional.of(invoice));

        // Act & Assert
        assertThatThrownBy(() ->
                invoiceService.cancelInvoice(invoiceId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Only unpaid generated invoices can be cancelled"
                );

        verify(invoiceRepository, never())
                .save(any());
    }


    // =========================================================
    // Helper methods
    // =========================================================

    private Order createOrder(UUID orderId) {

        Order order = new Order();

        order.setId(orderId);
        order.setOrderNumber("ORD-12345678");
        order.setBillingStatus(
                OrderBillingStatus.UNBILLED
        );
        order.setItems(new ArrayList<>());

        return order;
    }


    private OrderItem createOrderItem(
            Order order,
            int quantity,
            BigDecimal unitPrice
    ) {

        OrderItem item =
                new OrderItem();

        item.setId(UUID.randomUUID());
        item.setOrder(order);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setAddOns(new ArrayList<>());

        return item;
    }


    private Invoice captureSavedInvoice() {

        var captor =
                org.mockito.ArgumentCaptor
                        .forClass(Invoice.class);

        verify(invoiceRepository)
                .save(captor.capture());

        return captor.getValue();
    }
}