package com.thewildchild.management.order.controller;

import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.dto.response.OrderResponse;
import com.thewildchild.management.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestParam UUID diningSessionId
    ) {

        OrderResponse response =
                orderService.createOrder(diningSessionId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId
    ) {

        OrderResponse response =
                orderService.getOrderById(orderId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addOrderItem(
            @PathVariable UUID orderId,
            @Valid @RequestBody AddOrderItemRequest request
    ) {

        OrderResponse response =
                orderService.addOrderItem(
                        orderId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}