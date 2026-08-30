package com.thewildchild.management.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.order.dto.response.KotResponse;
import com.thewildchild.management.order.service.KotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = KotController.class)
@AutoConfigureMockMvc(addFilters = false)
class KotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KotService kotService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldGenerateKotSuccessfully()
            throws Exception {

        // Arrange
        UUID orderId = UUID.randomUUID();

        KotResponse response = new KotResponse();

        response.setOrderId(orderId);
        response.setOrderNumber("ORD-12345678");

        when(kotService.generateKot(orderId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/orders/{orderId}/kot", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Verify
        verify(kotService)
                .generateKot(orderId);
    }


    @Test
    void shouldRejectGenerateKotWhenOrderIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/orders/{orderId}/kot",
                                "invalid-uuid")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(kotService);
    }
}