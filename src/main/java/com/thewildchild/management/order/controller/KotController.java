package com.thewildchild.management.order.controller;

import com.thewildchild.management.order.dto.response.KotResponse;
import com.thewildchild.management.order.service.KotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class KotController {

    private final KotService kotService;

    @PostMapping("/{orderId}/kot")
    public ResponseEntity<KotResponse> generateKot(
            @PathVariable UUID orderId
    ) {

        KotResponse response =
                kotService.generateKot(orderId);

        return ResponseEntity.ok(response);
    }
}