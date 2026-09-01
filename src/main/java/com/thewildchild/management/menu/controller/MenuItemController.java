package com.thewildchild.management.menu.controller;

import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;
import com.thewildchild.management.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuItemService.createMenuItem(request));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(
                menuItemService.getMenuItemById(itemId)
        );
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(
                menuItemService.getAllMenuItems()
        );
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateMenuItemRequest request
    ) {
        return ResponseEntity.ok(
                menuItemService.updateMenuItem(itemId, request)
        );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable UUID itemId
    ) {
        menuItemService.deleteMenuItem(itemId);

        return ResponseEntity.noContent().build();
    }
}