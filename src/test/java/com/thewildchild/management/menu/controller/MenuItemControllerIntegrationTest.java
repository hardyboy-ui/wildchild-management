package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.role.Role;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MenuItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;


    private User user;

    private MenuCategory category;

    private MenuItem menuItem;

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
         * OWNER is allowed to access menu endpoints.
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


        /*
         * ---------------------------------------------------------
         * MENU CATEGORY
         * ---------------------------------------------------------
         */

        category = new MenuCategory();

        category.setName("Burgers");
        category.setDescription("Burger items");
        category.setActive(true);
        category.setDisplayOrder(1);

        category = menuCategoryRepository.saveAndFlush(category);


        /*
         * ---------------------------------------------------------
         * MENU ITEM
         * ---------------------------------------------------------
         */

        menuItem = new MenuItem();

        menuItem.setName("Test Burger");
        menuItem.setDescription("Test burger description");
        menuItem.setPrice(new BigDecimal("150.00"));
        menuItem.setImageUrl("https://example.com/burger.jpg");
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setActive(true);
        menuItem.setCategory(category);

        menuItem = menuItemRepository.saveAndFlush(menuItem);
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
    // CREATE MENU ITEM
    // =========================================================

    @Test
    void shouldCreateMenuItem() throws Exception {

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setName("Chicken Burger");
        request.setDescription("Grilled chicken burger");
        request.setPrice(new BigDecimal("200.00"));
        request.setImageUrl("https://example.com/chicken-burger.jpg");
        request.setFoodType(FoodType.NON_VEG);
        request.setCategoryId(category.getId());


        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/menu/items")
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
                        .andExpect(jsonPath("$.name")
                                .value("Chicken Burger"))
                        .andExpect(jsonPath("$.description")
                                .value("Grilled chicken burger"))
                        .andExpect(jsonPath("$.price")
                                .value(200.00))
                        .andExpect(jsonPath("$.foodType")
                                .value("NON_VEG"))
                        .andExpect(jsonPath("$.categoryId")
                                .value(category.getId().toString()))
                        .andExpect(jsonPath("$.categoryName")
                                .value("Burgers"))
                        .andReturn();


        /*
         * Verify database persistence.
         */

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );

        UUID createdItemId =
                UUID.fromString(
                        response.get("id").asText()
                );


        MenuItem savedItem =
                menuItemRepository.findById(createdItemId)
                        .orElseThrow();


        assertThat(savedItem.getName())
                .isEqualTo("Chicken Burger");

        assertThat(savedItem.getPrice())
                .isEqualByComparingTo("200.00");

        assertThat(savedItem.getFoodType())
                .isEqualTo(FoodType.NON_VEG);

        assertThat(savedItem.getCategory().getId())
                .isEqualTo(category.getId());
    }


    // =========================================================
    // GET MENU ITEM BY ID
    // =========================================================

    @Test
    void shouldGetMenuItemById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/menu/items/{itemId}",
                                menuItem.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(menuItem.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Test Burger"))
                .andExpect(jsonPath("$.description")
                        .value("Test burger description"))
                .andExpect(jsonPath("$.price")
                        .value(150.00))
                .andExpect(jsonPath("$.foodType")
                        .value("VEG"))
                .andExpect(jsonPath("$.categoryId")
                        .value(category.getId().toString()))
                .andExpect(jsonPath("$.categoryName")
                        .value("Burgers"));
    }


    // =========================================================
    // GET ALL MENU ITEMS
    // =========================================================

    @Test
    void shouldGetAllMenuItems() throws Exception {

        mockMvc.perform(
                        get("/api/v1/menu/items")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id")
                        .value(menuItem.getId().toString()))
                .andExpect(jsonPath("$[0].name")
                        .value("Test Burger"))
                .andExpect(jsonPath("$[0].price")
                        .value(150.00))
                .andExpect(jsonPath("$[0].foodType")
                        .value("VEG"))
                .andExpect(jsonPath("$[0].categoryId")
                        .value(category.getId().toString()))
                .andExpect(jsonPath("$[0].categoryName")
                        .value("Burgers"));
    }


    // =========================================================
    // UPDATE MENU ITEM
    // =========================================================

    @Test
    void shouldUpdateMenuItem() throws Exception {

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName("Updated Burger");
        request.setDescription("Updated burger description");
        request.setPrice(new BigDecimal("180.00"));
        request.setImageUrl("https://example.com/updated-burger.jpg");
        request.setFoodType(FoodType.NON_VEG);
        request.setCategoryId(category.getId());


        mockMvc.perform(
                        patch(
                                "/api/v1/menu/items/{itemId}",
                                menuItem.getId()
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
                        .value(menuItem.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Updated Burger"))
                .andExpect(jsonPath("$.description")
                        .value("Updated burger description"))
                .andExpect(jsonPath("$.price")
                        .value(180.00))
                .andExpect(jsonPath("$.foodType")
                        .value("NON_VEG"))
                .andExpect(jsonPath("$.categoryId")
                        .value(category.getId().toString()))
                .andExpect(jsonPath("$.categoryName")
                        .value("Burgers"));


        /*
         * Verify database state.
         */

        MenuItem updatedItem =
                menuItemRepository.findById(menuItem.getId())
                        .orElseThrow();


        assertThat(updatedItem.getName())
                .isEqualTo("Updated Burger");

        assertThat(updatedItem.getDescription())
                .isEqualTo("Updated burger description");

        assertThat(updatedItem.getPrice())
                .isEqualByComparingTo("180.00");

        assertThat(updatedItem.getFoodType())
                .isEqualTo(FoodType.NON_VEG);

        assertThat(updatedItem.getCategory().getId())
                .isEqualTo(category.getId());
    }


    // =========================================================
    // DELETE MENU ITEM
    // =========================================================

    @Test
    void shouldDeleteMenuItem() throws Exception {

        UUID itemId = menuItem.getId();


        mockMvc.perform(
                        delete(
                                "/api/v1/menu/items/{itemId}",
                                itemId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isNoContent());


        /*
         * MenuItem uses soft delete.
         *
         * Therefore the row should still exist,
         * but active should be false.
         */

        MenuItem deletedItem =
                menuItemRepository.findById(itemId)
                        .orElseThrow();


        assertThat(deletedItem.isActive())
                .isFalse();
    }


    // =========================================================
    // GET MENU ITEM - NOT FOUND
    // =========================================================

    @Test
    void shouldReturnNotFoundWhenMenuItemDoesNotExist()
            throws Exception {

        UUID randomItemId =
                UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/menu/items/{itemId}",
                                randomItemId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // CREATE MENU ITEM - VALIDATION FAILURE
    // =========================================================

    @Test
    void shouldRejectCreateMenuItemWithInvalidRequest()
            throws Exception {

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setName("");
        request.setDescription("Test");
        request.setPrice(new BigDecimal("-10.00"));
        request.setImageUrl("test");
        request.setFoodType(null);
        request.setCategoryId(null);


        mockMvc.perform(
                        post("/api/v1/menu/items")
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
                                "/api/v1/menu/items/{itemId}",
                                menuItem.getId()
                        )
                )
                .andExpect(status().isForbidden());
    }
}