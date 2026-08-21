package com.thewildchild.management.table.controller;

import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.service.CafeTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
public class CafeTableController {

    private final CafeTableService cafeTableService;

    @PostMapping
    public ResponseEntity<CafeTableResponse> createTable(
            @Valid @RequestBody CreateCafeTableRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cafeTableService.createTable(request));
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<CafeTableResponse> getTableById(
            @PathVariable UUID tableId
    ) {
        return ResponseEntity.ok(
                cafeTableService.getTableById(tableId)
        );
    }

    @GetMapping
    public ResponseEntity<List<CafeTableResponse>> getAllTables() {
        return ResponseEntity.ok(
                cafeTableService.getAllTables()
        );
    }

    @PatchMapping("/{tableId}")
    public ResponseEntity<CafeTableResponse> updateTable(
            @PathVariable UUID tableId,
            @Valid @RequestBody UpdateCafeTableRequest request
    ) {
        return ResponseEntity.ok(
                cafeTableService.updateTable(tableId, request)
        );
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable UUID tableId
    ) {
        cafeTableService.deleteTable(tableId);

        return ResponseEntity.noContent().build();
    }
}