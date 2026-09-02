package com.thewildchild.management.config;

import com.thewildchild.management.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(
                                // Swagger UI
                                "/swagger-ui/**",
                                "/swagger-ui.html",

                                // OpenAPI JSON
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/v1/users/**")
                        .hasAnyRole("SUPER_ADMIN", "OWNER")
                        // ==================== MENU ITEMS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menu/items",
                                "/api/v1/menu/items/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(HttpMethod.POST, "/api/v1/menu/items")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/menu/items/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/items/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )


                        // ==================== MENU CATEGORIES ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menu/categories",
                                "/api/v1/menu/categories/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(HttpMethod.POST, "/api/v1/menu/categories")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/menu/categories/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/categories/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )



                        // ==================== MENU ADD-ONS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menu/add-ons",
                                "/api/v1/menu/add-ons/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(HttpMethod.POST, "/api/v1/menu/add-ons")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/menu/add-ons/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/add-ons/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )


                        // ==================== MENU ITEM ADD-ONS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menu/items/*/add-ons"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/menu/items/*/add-ons/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/menu/items/*/add-ons/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )


                        // ==================== TABLES ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/tables",
                                "/api/v1/tables/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(HttpMethod.POST, "/api/v1/tables")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tables/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tables/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )


                        // ==================== DINING SESSIONS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/dining-sessions",
                                "/api/v1/dining-sessions/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(HttpMethod.POST, "/api/v1/dining-sessions")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/dining-sessions/*/close"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER",
                                "WAITER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/dining-sessions/*/cancel"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )


                        // ==================== INVOICES ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/invoices/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/invoices/order/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/invoices/dining-session/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/invoices/*/cancel"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER"
                        )

                        // ==================== PAYMENTS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/payments/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/payments/invoice/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )


                        // ==================== REPORTS ====================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/reports/**"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "OWNER",
                                "MANAGER",
                                "CASHIER"
                        )

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}