package com.thewildchild.management.menu.controller;

import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.service.AddOnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/add-ons")
@RequiredArgsConstructor
public class AddOnController {

    private final AddOnService addOnService;

    @PostMapping
    public ResponseEntity<AddOnResponse> createAddOn(
            @Valid @RequestBody CreateAddOnRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addOnService.createAddOn(request));
    }

    @GetMapping("/{addOnId}")
    public ResponseEntity<AddOnResponse> getAddOnById(
            @PathVariable UUID addOnId
    ) {
        return ResponseEntity.ok(
                addOnService.getAddOnById(addOnId)
        );
    }

    @GetMapping
    public ResponseEntity<List<AddOnResponse>> getAllAddOns() {
        return ResponseEntity.ok(
                addOnService.getAllAddOns()
        );
    }

    @PatchMapping("/{addOnId}")
    public ResponseEntity<AddOnResponse> updateAddOn(
            @PathVariable UUID addOnId,
            @Valid @RequestBody UpdateAddOnRequest request
    ) {
        return ResponseEntity.ok(
                addOnService.updateAddOn(addOnId, request)
        );
    }

    @DeleteMapping("/{addOnId}")
    public ResponseEntity<Void> deleteAddOn(
            @PathVariable UUID addOnId
    ) {
        addOnService.deleteAddOn(addOnId);

        return ResponseEntity.noContent().build();
    }
}