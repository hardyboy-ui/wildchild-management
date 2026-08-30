package com.thewildchild.management.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.order.dto.request.AddOrderItemAddOnRequest;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.dto.response.OrderResponse;
import com.thewildchild.management.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = OrderController.class
)
@AutoConfigureMockMvc(addFilters = false)

class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;



    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {

        UUID diningSessionId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        OrderResponse response = new OrderResponse();
        response.setId(orderId);
        response.setOrderNumber("ORD-12345678");

        when(orderService.createOrder(diningSessionId))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/orders")
                                .param(
                                        "diningSessionId",
                                        diningSessionId.toString()
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(orderId.toString())
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .value("ORD-12345678")
                );

        verify(orderService)
                .createOrder(diningSessionId);
    }


    // =========================================================
    // GET ORDER
    // =========================================================

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        OrderResponse response = new OrderResponse();
        response.setId(orderId);
        response.setOrderNumber("ORD-12345678");

        when(orderService.getOrderById(orderId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/orders/{orderId}", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(orderId.toString())
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .value("ORD-12345678")
                );

        verify(orderService)
                .getOrderById(orderId);
    }


    // =========================================================
    // CLOSE ORDER
    // =========================================================

    @Test
    void shouldCloseOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        OrderResponse response = new OrderResponse();
        response.setId(orderId);
        response.setOrderNumber("ORD-12345678");

        when(orderService.closeOrder(orderId))
                .thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/orders/{orderId}/close",
                                orderId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(orderId.toString())
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .value("ORD-12345678")
                );

        verify(orderService)
                .closeOrder(orderId);
    }


    // =========================================================
    // ADD ORDER ITEM
    // =========================================================

    @Test
    void shouldAddOrderItemSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(menuItemId);
        request.setQuantity(2);
        request.setSpecialInstructions(
                "Less spicy"
        );

        OrderResponse response =
                new OrderResponse();

        response.setId(orderId);
        response.setOrderNumber("ORD-12345678");

        when(orderService.addOrderItem(
                eq(orderId),
                any(AddOrderItemRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
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
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(orderId.toString())
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .value("ORD-12345678")
                );

        verify(orderService)
                .addOrderItem(
                        eq(orderId),
                        any(AddOrderItemRequest.class)
                );
    }


    // =========================================================
    // ADD ORDER ITEM - INVALID MENU ITEM ID
    // =========================================================

    @Test
    void shouldRejectAddOrderItemWhenMenuItemIdIsMissing()
            throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setQuantity(2);

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
                                UUID.randomUUID()
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
    }


    // =========================================================
    // ADD ORDER ITEM - INVALID QUANTITY
    // =========================================================

    @Test
    void shouldRejectAddOrderItemWhenQuantityIsZero()
            throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(0);

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
                                UUID.randomUUID()
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
    }


    // =========================================================
    // ADD ORDER ITEM - MISSING QUANTITY
    // =========================================================

    @Test
    void shouldRejectAddOrderItemWhenQuantityIsMissing()
            throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(UUID.randomUUID());

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
                                UUID.randomUUID()
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
    }


    // =========================================================
    // ADD ORDER ITEM - SPECIAL INSTRUCTIONS TOO LONG
    // =========================================================

    @Test
    void shouldRejectAddOrderItemWhenSpecialInstructionsTooLong()
            throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(1);

        request.setSpecialInstructions(
                "a".repeat(501)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
                                UUID.randomUUID()
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
    }


    // =========================================================
    // ADD ORDER ITEM - INVALID ADD-ON
    // =========================================================

    @Test
    void shouldRejectAddOrderItemWhenAddOnIdIsMissing()
            throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(1);

        AddOrderItemAddOnRequest addOnRequest =
                new AddOrderItemAddOnRequest();

        addOnRequest.setAddOnId(null);

        request.setAddOns(
                List.of(addOnRequest)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/orders/{orderId}/items",
                                UUID.randomUUID()
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
    }
}