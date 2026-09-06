package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.entity.MenuItemAddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MenuItemAddOnControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private MenuItemAddOnRepository menuItemAddOnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MenuItem menuItem;
    private AddOn addOn;
    private MenuItemAddOn mapping;

    private String token;


    @BeforeEach
    void setUp() {

        /*
         * ---------------------------------------------------------
         * MENU CATEGORY
         * ---------------------------------------------------------
         */

        MenuCategory category = new MenuCategory();

        category.setName("Burgers");
        category.setDescription("Burger items");
        category.setDisplayOrder(1);
        category.setActive(true);

        category = menuCategoryRepository.saveAndFlush(category);


        /*
         * ---------------------------------------------------------
         * MENU ITEM
         * ---------------------------------------------------------
         */

        menuItem = new MenuItem();

        menuItem.setName("Test Burger");
        menuItem.setDescription("Test burger");
        menuItem.setPrice(new BigDecimal("100.00"));
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setCategory(category);
        menuItem.setActive(true);

        menuItem = menuItemRepository.saveAndFlush(menuItem);


        /*
         * ---------------------------------------------------------
         * ADD-ON
         * ---------------------------------------------------------
         */

        addOn = new AddOn();

        addOn.setName("Extra Cheese");
        addOn.setDescription("Additional cheese");
        addOn.setPrice(new BigDecimal("30.00"));
        addOn.setActive(true);

        addOn = addOnRepository.saveAndFlush(addOn);


        /*
         * ---------------------------------------------------------
         * AUTHENTICATED USER
         * ---------------------------------------------------------
         *
         * Keep this identical to your already-working
         * UserControllerIntegrationTest authentication setup.
         * ---------------------------------------------------------
         */

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Admin");
        user.setEmail("menu-item-addon-test@example.com");
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
    // ADD ADD-ON
    // =========================================================

    @Test
    void shouldAddAddOnToMenuItem() throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated());


        /*
         * Verify the relationship was actually persisted.
         */

        boolean exists =
                menuItemAddOnRepository
                        .existsByMenuItemIdAndAddOnId(
                                menuItem.getId(),
                                addOn.getId()
                        );

        assertThat(exists)
                .isTrue();
    }


    // =========================================================
    // GET ADD-ONS
    // =========================================================

    @Test
    void shouldGetAddOnsForMenuItem()
            throws Exception {

        /*
         * Create mapping first.
         */

        mapping = new MenuItemAddOn();

        mapping.setMenuItem(menuItem);
        mapping.setAddOn(addOn);

        menuItemAddOnRepository.saveAndFlush(mapping);


        mockMvc.perform(
                        get(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].id")
                                .value(addOn.getId().toString())
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$[0].description")
                                .value("Additional cheese")
                )
                .andExpect(
                        jsonPath("$[0].price")
                                .value(30.00)
                );
    }


    // =========================================================
    // REMOVE ADD-ON
    // =========================================================

    @Test
    void shouldRemoveAddOnFromMenuItem()
            throws Exception {

        /*
         * Create mapping first.
         */

        mapping = new MenuItemAddOn();

        mapping.setMenuItem(menuItem);
        mapping.setAddOn(addOn);

        mapping =
                menuItemAddOnRepository.saveAndFlush(mapping);


        mockMvc.perform(
                        delete(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());


        /*
         * Verify relationship was removed.
         */

        boolean exists =
                menuItemAddOnRepository
                        .existsByMenuItemIdAndAddOnId(
                                menuItem.getId(),
                                addOn.getId()
                        );

        assertThat(exists)
                .isFalse();
    }


    // =========================================================
    // DUPLICATE ADD-ON
    // =========================================================

    @Test
    void shouldRejectDuplicateAddOn()
            throws Exception {

        /*
         * Attach the add-on once.
         */

        mapping = new MenuItemAddOn();

        mapping.setMenuItem(menuItem);
        mapping.setAddOn(addOn);

        menuItemAddOnRepository.saveAndFlush(mapping);


        /*
         * Try attaching it again.
         */

        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isBadRequest());


        /*
         * There should still be only one mapping.
         */

        long count =
                menuItemAddOnRepository
                        .findAll()
                        .stream()
                        .filter(item ->
                                item.getMenuItem()
                                        .getId()
                                        .equals(menuItem.getId())
                                        &&
                                        item.getAddOn()
                                                .getId()
                                                .equals(addOn.getId())
                        )
                        .count();

        assertThat(count)
                .isEqualTo(1);
    }


    // =========================================================
    // REMOVE NON-EXISTING MAPPING
    // =========================================================

    @Test
    void shouldReturnNotFoundWhenRemovingUnattachedAddOn()
            throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // UNKNOWN MENU ITEM
    // =========================================================

    @Test
    void shouldReturnNotFoundForUnknownMenuItem()
            throws Exception {

        UUID unknownMenuItemId =
                UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/menu/items/"
                                        + unknownMenuItemId
                                        + "/add-ons"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // UNKNOWN ADD-ON
    // =========================================================

    @Test
    void shouldReturnNotFoundForUnknownAddOn()
            throws Exception {

        UUID unknownAddOnId =
                UUID.randomUUID();


        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + unknownAddOnId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // INACTIVE MENU ITEM
    // =========================================================

    @Test
    void shouldRejectInactiveMenuItem()
            throws Exception {

        menuItem.setActive(false);

        menuItemRepository.saveAndFlush(menuItem);


        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }


    // =========================================================
    // INACTIVE ADD-ON
    // =========================================================

    @Test
    void shouldRejectInactiveAddOn()
            throws Exception {

        addOn.setActive(false);

        addOnRepository.saveAndFlush(addOn);


        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/"
                                        + menuItem.getId()
                                        + "/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }
}