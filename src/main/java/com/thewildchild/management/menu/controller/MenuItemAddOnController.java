package com.thewildchild.management.menu.controller;

import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.service.MenuItemAddOnService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/items/{menuItemId}/add-ons")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MenuItemAddOnController {

    private final MenuItemAddOnService menuItemAddOnService;

    @PostMapping("/{addOnId}")
    public ResponseEntity<Void> addAddOnToMenuItem(
            @PathVariable UUID menuItemId,
            @PathVariable UUID addOnId
    ) {
        menuItemAddOnService.addAddOnToMenuItem(
                menuItemId,
                addOnId
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{addOnId}")
    public ResponseEntity<Void> removeAddOnFromMenuItem(
            @PathVariable UUID menuItemId,
            @PathVariable UUID addOnId
    ) {
        menuItemAddOnService.removeAddOnFromMenuItem(
                menuItemId,
                addOnId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AddOnResponse>> getAddOnsForMenuItem(
            @PathVariable UUID menuItemId
    ) {
        return ResponseEntity.ok(
                menuItemAddOnService.getAddOnsForMenuItem(menuItemId)
        );
    }
}