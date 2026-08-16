package com.thewildchild.management.auth.service.impl;

import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String accessToken = jwtService.generateToken(
                (UserDetails) authentication.getPrincipal()
        );

        return new LoginResponse(accessToken, "Bearer");
    }
}