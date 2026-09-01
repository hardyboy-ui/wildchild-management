package com.thewildchild.management.menu.controller;

import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;
import com.thewildchild.management.menu.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping
    public ResponseEntity<MenuCategoryResponse> createCategory(
            @Valid @RequestBody CreateMenuCategoryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuCategoryService.createCategory(request));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<MenuCategoryResponse> getCategoryById(
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(
                menuCategoryService.getCategoryById(categoryId)
        );
    }

    @GetMapping
    public ResponseEntity<List<MenuCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(
                menuCategoryService.getAllCategories()
        );
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<MenuCategoryResponse> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateMenuCategoryRequest request
    ) {
        return ResponseEntity.ok(
                menuCategoryService.updateCategory(categoryId, request)
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId
    ) {
        menuCategoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}