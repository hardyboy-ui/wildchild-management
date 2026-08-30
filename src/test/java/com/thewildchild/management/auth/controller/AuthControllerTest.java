package com.thewildchild.management.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldLoginSuccessfully() throws Exception {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("admin@example.com");
        request.setPassword("password123");

        LoginResponse response =
                new LoginResponse(
                        "jwt-token",
                        "Bearer"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accessToken")
                                .value("jwt-token")
                )
                .andExpect(
                        jsonPath("$.tokenType")
                                .value("Bearer")
                );

        // Verify
        verify(authService)
                .login(any(LoginRequest.class));
    }


    @Test
    void shouldRejectWhenEmailIsBlank() throws Exception {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("");
        request.setPassword("password123");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(authService);
    }


    @Test
    void shouldRejectWhenEmailFormatIsInvalid() throws Exception {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("invalid-email");
        request.setPassword("password123");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(authService);
    }


    @Test
    void shouldRejectWhenPasswordIsBlank() throws Exception {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("admin@example.com");
        request.setPassword("");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(authService);
    }


    @Test
    void shouldRejectWhenEmailAndPasswordAreBlank() throws Exception {

        // Arrange
        LoginRequest request = new LoginRequest();

        request.setEmail("");
        request.setPassword("");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(authService);
    }
}