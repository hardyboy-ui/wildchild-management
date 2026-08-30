package com.thewildchild.management.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.payment.dto.request.CreatePaymentRequest;
import com.thewildchild.management.payment.dto.response.PaymentResponse;
import com.thewildchild.management.payment.entity.PaymentMethod;
import com.thewildchild.management.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PaymentController.class
)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldCreatePaymentSuccessfully()
            throws Exception {

        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(BigDecimal.valueOf(500));

        PaymentResponse response =
                new PaymentResponse();

        response.setId(UUID.randomUUID());
        response.setInvoiceId(invoiceId);
        response.setPaymentMethod(PaymentMethod.CASH);
        response.setAmount(BigDecimal.valueOf(500));

        when(paymentService.createPayment(
                eq(invoiceId),
                any(CreatePaymentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/payments/invoice/{invoiceId}",
                                invoiceId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.invoiceId")
                                .value(invoiceId.toString())
                )
                .andExpect(
                        jsonPath("$.paymentMethod")
                                .value("CASH")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500)
                );

        verify(paymentService)
                .createPayment(
                        eq(invoiceId),
                        any(CreatePaymentRequest.class)
                );
    }


    @Test
    void shouldRejectCreatePaymentWhenPaymentMethodIsMissing()
            throws Exception {

        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setAmount(BigDecimal.valueOf(500));

        mockMvc.perform(
                        post(
                                "/api/v1/payments/invoice/{invoiceId}",
                                invoiceId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }


    @Test
    void shouldRejectCreatePaymentWhenAmountIsMissing()
            throws Exception {

        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);

        mockMvc.perform(
                        post(
                                "/api/v1/payments/invoice/{invoiceId}",
                                invoiceId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }


    @Test
    void shouldRejectCreatePaymentWhenAmountIsZero()
            throws Exception {

        UUID invoiceId = UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(BigDecimal.ZERO);

        mockMvc.perform(
                        post(
                                "/api/v1/payments/invoice/{invoiceId}",
                                invoiceId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }


    @Test
    void shouldGetPaymentByIdSuccessfully()
            throws Exception {

        UUID paymentId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();

        PaymentResponse response =
                new PaymentResponse();

        response.setId(paymentId);
        response.setInvoiceId(invoiceId);
        response.setPaymentMethod(PaymentMethod.CASH);
        response.setAmount(BigDecimal.valueOf(500));

        when(paymentService.getPaymentById(paymentId))
                .thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/payments/{paymentId}",
                                paymentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(paymentId.toString())
                )
                .andExpect(
                        jsonPath("$.invoiceId")
                                .value(invoiceId.toString())
                )
                .andExpect(
                        jsonPath("$.paymentMethod")
                                .value("CASH")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500)
                );

        verify(paymentService)
                .getPaymentById(paymentId);
    }


    @Test
    void shouldRejectGetPaymentWhenPaymentIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/payments/{paymentId}",
                                "invalid-uuid"
                        )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }
}