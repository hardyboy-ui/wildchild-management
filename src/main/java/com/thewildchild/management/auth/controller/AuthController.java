package com.thewildchild.management.auth.controller;

import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;
import com.thewildchild.management.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {


        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}