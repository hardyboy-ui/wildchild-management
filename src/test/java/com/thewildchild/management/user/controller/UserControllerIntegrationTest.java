package com.thewildchild.management.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    private String accessToken;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {

        /*
         * ---------------------------------------------------------
         * TEST USER
         * ---------------------------------------------------------
         *
         * OWNER is required because SecurityConfig allows
         * /api/v1/users/** only for SUPER_ADMIN and OWNER.
         */

        user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testuser@gmail.com");
        user.setPhoneNumber("9876543210");

        user.setPasswordHash(
                passwordEncoder.encode("Password@123")
        );

        user.setRole(Role.OWNER);
        user.setActive(true);

        user = userRepository.saveAndFlush(user);


        /*
         * ---------------------------------------------------------
         * LOGIN THROUGH REAL AUTH API
         * ---------------------------------------------------------
         */

        accessToken = loginAndGetToken();
    }


    // =========================================================
    // HELPER - LOGIN
    // =========================================================

    private String loginAndGetToken() throws Exception {

        LoginRequest request = new LoginRequest();

        request.setEmail("testuser@gmail.com");
        request.setPassword("Password@123");


        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(request)
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();


        /*
         * Read JWT from LoginResponse.
         *
         * We read the JSON directly so this test does not
         * depend on a particular LoginResponse constructor.
         */

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );


        return response
                .get("accessToken")
                .asText();
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    @Test
    void shouldCreateUser() throws Exception {

        CreateUserRequest request =
                new CreateUserRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@gmail.com");
        request.setPhoneNumber("9123456789");
        request.setPassword("Password@123");
        request.setRole(Role.OWNER);


        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/users")
                                        .header(
                                                "Authorization",
                                                "Bearer " + accessToken
                                        )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(request)
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.firstName")
                                .value("John"))
                        .andExpect(jsonPath("$.lastName")
                                .value("Doe"))
                        .andExpect(jsonPath("$.email")
                                .value("john@gmail.com"))
                        .andExpect(jsonPath("$.phoneNumber")
                                .value("9123456789"))
                        .andExpect(jsonPath("$.role")
                                .value("OWNER"))
                        .andExpect(jsonPath("$.active")
                                .value(true))
                        .andReturn();


        /*
         * Verify database persistence.
         */

        String responseBody =
                result.getResponse()
                        .getContentAsString();

        JsonNode response =
                objectMapper.readTree(responseBody);

        UUID createdUserId =
                UUID.fromString(
                        response.get("id").asText()
                );


        User savedUser =
                userRepository.findById(createdUserId)
                        .orElseThrow();


        assertThat(savedUser.getFirstName())
                .isEqualTo("John");

        assertThat(savedUser.getEmail())
                .isEqualTo("john@gmail.com");

        /*
         * Password must be stored as a BCrypt hash,
         * not plain text.
         */

        assertThat(savedUser.getPasswordHash())
                .isNotEqualTo("Password@123");

        assertThat(
                passwordEncoder.matches(
                        "Password@123",
                        savedUser.getPasswordHash()
                )
        )
                .isTrue();
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Test
    void shouldGetUserById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/users/{id}",
                                user.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(user.getId().toString()))
                .andExpect(jsonPath("$.firstName")
                        .value("Test"))
                .andExpect(jsonPath("$.lastName")
                        .value("User"))
                .andExpect(jsonPath("$.email")
                        .value("testuser@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber")
                        .value("9876543210"))
                .andExpect(jsonPath("$.role")
                        .value("OWNER"))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

//    @Test
//    void shouldGetAllUsers() throws Exception {
//
//        mockMvc.perform(
//                        get("/api/v1/users")
//                                .header(
//                                        "Authorization",
//                                        "Bearer " + accessToken
//                                )
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].id")
//                        .value(user.getId().toString()))
//                .andExpect(jsonPath("$[0].firstName")
//                        .value("Hardik"))
//                .andExpect(jsonPath("$[0].email")
//                        .value("owner@thewildchild.com"));
//    }


    // =========================================================
    // UPDATE USER
    // =========================================================

    @Test
    void shouldUpdateUser() throws Exception {

        UpdateUserRequest request =
                new UpdateUserRequest();

        request.setFirstName("Updated");
        request.setLastName("Name");
        request.setPhoneNumber("9999999999");
        request.setActive(false);


        mockMvc.perform(
                        put(
                                "/api/v1/users/{id}",
                                user.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(user.getId().toString()))
                .andExpect(jsonPath("$.firstName")
                        .value("Updated"))
                .andExpect(jsonPath("$.lastName")
                        .value("Name"))
                .andExpect(jsonPath("$.phoneNumber")
                        .value("9999999999"))
                .andExpect(jsonPath("$.active")
                        .value(false));


        /*
         * Verify database state.
         */

        User updatedUser =
                userRepository.findById(user.getId())
                        .orElseThrow();


        assertThat(updatedUser.getFirstName())
                .isEqualTo("Updated");

        assertThat(updatedUser.getLastName())
                .isEqualTo("Name");

        assertThat(updatedUser.getPhoneNumber())
                .isEqualTo("9999999999");

        assertThat(updatedUser.getActive())
                .isFalse();
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    @Test
    void shouldDeleteUser() throws Exception {

        UUID userId = user.getId();


        mockMvc.perform(
                        delete(
                                "/api/v1/users/{id}",
                                userId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isNoContent());


        /*
         * Verify deletion.
         */

        assertThat(
                userRepository.findById(userId)
        )
                .isEmpty();
    }


    // =========================================================
    // GET USER - NOT FOUND
    // =========================================================

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist()
            throws Exception {

        UUID randomUserId =
                UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/users/{id}",
                                randomUserId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // CREATE USER - VALIDATION FAILURE
    // =========================================================

    @Test
    void shouldRejectCreateUserWithInvalidRequest()
            throws Exception {

        CreateUserRequest request =
                new CreateUserRequest();

        request.setFirstName("");
        request.setLastName("Doe");
        request.setEmail("invalid-email");
        request.setPhoneNumber("123");
        request.setPassword("");
        request.setRole(null);


        mockMvc.perform(
                        post("/api/v1/users")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    // =========================================================
    // UNAUTHENTICATED REQUEST
    // =========================================================

    @Test
    void shouldRejectUnauthenticatedRequest()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/users/{id}",
                                user.getId()
                        )
                )
                .andExpect(status().isForbidden());
    }
}