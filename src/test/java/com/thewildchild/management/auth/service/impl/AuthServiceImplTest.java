package com.thewildchild.management.auth.service.impl;

import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;
import com.thewildchild.management.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        UserDetails user = User.withUsername(request.getEmail())
                .password(request.getPassword())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        )).thenReturn(authentication);

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken())
                .isEqualTo("test-jwt-token");
        assertThat(response.getTokenType())
                .isEqualTo("Bearer");

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException("Invalid credentials")
        );

        // Act & Assert
        assertThatThrownBy(() ->
                authService.login(request)
        )
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        // Verify
        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldPropagateExceptionWhenTokenGenerationFails() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        UserDetails user = User.withUsername(request.getEmail())
                .password(request.getPassword())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.generateToken(user))
                .thenThrow(new IllegalStateException("JWT generation failed"));

        // Act & Assert
        assertThatThrownBy(() ->
                authService.login(request)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT generation failed");

        // Verify
        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void shouldAuthenticateUsingProvidedCredentials() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        UserDetails user = User.withUsername(request.getEmail())
                .password(request.getPassword())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        // Act
        authService.login(request);

        // Verify
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
                .authenticate(captor.capture());

        UsernamePasswordAuthenticationToken capturedToken =
                captor.getValue();

        // Assert
        assertThat(capturedToken.getPrincipal())
                .isEqualTo(request.getEmail());

        assertThat(capturedToken.getCredentials())
                .isEqualTo(request.getPassword());
    }
}
