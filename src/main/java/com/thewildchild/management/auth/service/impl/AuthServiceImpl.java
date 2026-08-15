package com.thewildchild.management.auth.service.impl;

import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;
import com.thewildchild.management.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return new LoginResponse("Authentication successful");
    }
}