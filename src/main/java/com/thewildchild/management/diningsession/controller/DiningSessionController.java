package com.thewildchild.management.diningsession.controller;

import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.service.DiningSessionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dining-sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DiningSessionController {

    private final DiningSessionService diningSessionService;

    @PostMapping
    public ResponseEntity<DiningSessionResponse> openSession(
            @Valid @RequestBody CreateDiningSessionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(diningSessionService.openSession(request));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<DiningSessionResponse> getSessionById(
            @PathVariable UUID sessionId
    ) {
        return ResponseEntity.ok(
                diningSessionService.getSessionById(sessionId)
        );
    }

    @GetMapping("/table/{tableId}/open")
    public ResponseEntity<DiningSessionResponse> getOpenSessionByTable(
            @PathVariable UUID tableId
    ) {
        return ResponseEntity.ok(
                diningSessionService.getOpenSessionByTable(tableId)
        );
    }

    @GetMapping("/open")
    public ResponseEntity<List<DiningSessionResponse>> getOpenSessions() {
        return ResponseEntity.ok(
                diningSessionService.getOpenSessions()
        );
    }

    @PostMapping("/{sessionId}/close")
    public ResponseEntity<DiningSessionResponse> closeSession(
            @PathVariable UUID sessionId
    ) {
        return ResponseEntity.ok(
                diningSessionService.closeSession(sessionId)
        );
    }

    @PostMapping("/{sessionId}/cancel")
    public ResponseEntity<DiningSessionResponse> cancelSession(
            @PathVariable UUID sessionId
    ) {
        return ResponseEntity.ok(
                diningSessionService.cancelSession(sessionId)
        );
    }
}