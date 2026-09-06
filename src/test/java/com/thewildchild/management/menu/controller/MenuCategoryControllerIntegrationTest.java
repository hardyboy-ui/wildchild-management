package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MenuCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MenuCategory category;

    private String token;


    @BeforeEach
    void setUp() {

        /*
         * ---------------------------------------------------------
         * CATEGORY
         * ---------------------------------------------------------
         */

        category = new MenuCategory();

        category.setName("Burgers");
        category.setDescription("All burger items");
        category.setDisplayOrder(1);
        category.setActive(true);

        category = menuCategoryRepository.saveAndFlush(category);


        /*
         * ---------------------------------------------------------
         * AUTHENTICATED USER
         * ---------------------------------------------------------
         *
         * Use the same authentication setup that worked in
         * UserControllerIntegrationTest.
         * ---------------------------------------------------------
         */

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Admin");
        user.setEmail("category-test@example.com");
        user.setPhoneNumber("9876543210");
        user.setPasswordHash(
                passwordEncoder.encode("password123")
        );
        user.setRole(Role.OWNER);
        user.setActive(true);

        user = userRepository.saveAndFlush(user);


        /*
         * ---------------------------------------------------------
         * JWT
         * ---------------------------------------------------------
         */

        token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .build()
        );
    }


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateCategory() throws Exception {

        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("Pizza");
        request.setDescription("Pizza items");
        request.setDisplayOrder(2);


        mockMvc.perform(
                        post("/api/v1/menu/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(
                        jsonPath("$.description")
                                .value("Pizza items")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(2)
                );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetCategoryById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/menu/categories/"
                                        + category.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(category.getId().toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Burgers")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("All burger items")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllCategories() throws Exception {

        mockMvc.perform(
                        get("/api/v1/menu/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Burgers")
                );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateCategory() throws Exception {

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("Premium Burgers");
        request.setDescription(
                "Premium burger items"
        );
        request.setDisplayOrder(5);


        mockMvc.perform(
                        patch(
                                "/api/v1/menu/categories/"
                                        + category.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(category.getId().toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Premium Burgers")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Premium burger items")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(5)
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteCategory() throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/v1/menu/categories/"
                                        + category.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());


        MenuCategory deletedCategory =
                menuCategoryRepository
                        .findById(category.getId())
                        .orElseThrow();

        /*
         * Your service performs a soft delete.
         */

        org.assertj.core.api.Assertions
                .assertThat(deletedCategory.isActive())
                .isFalse();
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    @Test
    void shouldRejectCreateCategoryWithoutName()
            throws Exception {

        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("");
        request.setDescription("Invalid category");
        request.setDisplayOrder(1);


        mockMvc.perform(
                        post("/api/v1/menu/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectCreateCategoryWithoutDisplayOrder()
            throws Exception {

        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("Pizza");
        request.setDescription("Pizza items");
        request.setDisplayOrder(null);


        mockMvc.perform(
                        post("/api/v1/menu/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }


    // =========================================================
    // NOT FOUND
    // =========================================================

    @Test
    void shouldReturnNotFoundForUnknownCategory()
            throws Exception {

        UUID unknownId = UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/menu/categories/"
                                        + unknownId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnNotFoundWhenDeletingUnknownCategory()
            throws Exception {

        UUID unknownId = UUID.randomUUID();


        mockMvc.perform(
                        delete(
                                "/api/v1/menu/categories/"
                                        + unknownId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }
}