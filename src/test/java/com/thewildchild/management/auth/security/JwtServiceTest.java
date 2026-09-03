package com.thewildchild.management.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        String secret = Base64.getEncoder()
                .encodeToString(
                        "my-super-secret-key-that-is-long-enough-for-hs256"
                                .getBytes()
                );

        jwtService = new JwtService(
                secret,
                3600000
        );
    }

    @Test
    void shouldGenerateTokenSuccessfully() {

        // Arrange
        UserDetails user = User.withUsername("john@example.com")
                .password("password123")
                .roles("CASHIER")
                .build();

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void shouldExtractUsernameSuccessfully() {

        // Arrange
        UserDetails user = User.withUsername("john@example.com")
                .password("password123")
                .roles("CASHIER")
                .build();

        String token = jwtService.generateToken(user);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertThat(username)
                .isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnTrueForValidToken() {

        // Arrange
        UserDetails user = User.withUsername("john@example.com")
                .password("password123")
                .roles("CASHIER")
                .build();

        String token = jwtService.generateToken(user);

        // Act
        boolean valid = jwtService.isTokenValid(token, user);

        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTokenBelongsToDifferentUser() {

        // Arrange
        UserDetails tokenUser = User.withUsername("john@example.com")
                .password("password123")
                .roles("CASHIER")
                .build();

        UserDetails differentUser = User.withUsername("alice@example.com")
                .password("password123")
                .roles("CASHIER")
                .build();

        String token = jwtService.generateToken(tokenUser);

        // Act
        boolean valid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertThat(valid).isFalse();
    }

//    @Test
//    void shouldReturnFalseForExpiredToken() {
//
//        // Arrange
//        UserDetails user = User.withUsername("john@example.com")
//                .password("password123")
//                .roles("CASHIER")
//                .build();
//
//        String secret = Base64.getEncoder()
//                .encodeToString(
//                        "my-super-secret-key-that-is-long-enough-for-hs256"
//                                .getBytes()
//                );
//
//        JwtService expiredJwtService =
//                new JwtService(secret, -1);
//
//        String token = expiredJwtService.generateToken(user);
//
//        // Act
//        boolean valid = expiredJwtService.isTokenValid(token, user);
//
//        // Assert
//        assertThat(valid).isFalse();
//    }
}