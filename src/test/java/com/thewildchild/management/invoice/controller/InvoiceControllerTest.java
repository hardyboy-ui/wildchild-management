package com.thewildchild.management.invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.invoice.dto.request.GenerateInvoiceRequest;
import com.thewildchild.management.invoice.dto.response.InvoiceResponse;
import com.thewildchild.management.invoice.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = InvoiceController.class
)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InvoiceService invoiceService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldGenerateOrderInvoiceSuccessfully()
            throws Exception {

        // Arrange
        UUID orderId = UUID.randomUUID();

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountType(null);
        request.setDiscountValue(null);
        request.setTaxRate(BigDecimal.ZERO);

        InvoiceResponse response =
                new InvoiceResponse();

        response.setId(UUID.randomUUID());
        response.setInvoiceNumber("INV-12345678");
        response.setGrandTotal(
                BigDecimal.valueOf(100)
        );

        when(invoiceService.generateOrderInvoice(
                eq(orderId),
                any(GenerateInvoiceRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post(
                                "/api/v1/invoices/order/{orderId}",
                                orderId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk());

        // Verify
        verify(invoiceService)
                .generateOrderInvoice(
                        eq(orderId),
                        any(GenerateInvoiceRequest.class)
                );
    }


    @Test
    void shouldRejectOrderInvoiceWhenDiscountValueIsNegative()
            throws Exception {

        UUID orderId = UUID.randomUUID();

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setDiscountValue(
                BigDecimal.valueOf(-10)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/invoices/order/{orderId}",
                                orderId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }


    @Test
    void shouldRejectOrderInvoiceWhenTaxRateExceeds100()
            throws Exception {

        UUID orderId = UUID.randomUUID();

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        request.setTaxRate(
                BigDecimal.valueOf(101)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/invoices/order/{orderId}",
                                orderId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }


    @Test
    void shouldRejectOrderInvoiceWhenOrderIdIsInvalid()
            throws Exception {

        GenerateInvoiceRequest request =
                new GenerateInvoiceRequest();

        mockMvc.perform(
                        post(
                                "/api/v1/invoices/order/{orderId}",
                                "invalid-uuid"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }
}