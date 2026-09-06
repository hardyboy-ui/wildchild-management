package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
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
class AddOnControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private AddOn addOn;

    private String token;


    @BeforeEach
    void setUp() {

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
         * TEST USER
         * ---------------------------------------------------------
         *
         * Keep this authentication setup identical to the one
         * used in your working UserControllerIntegrationTest.
         * ---------------------------------------------------------
         */

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Admin");
        user.setEmail("addon-test@example.com");
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
    void shouldCreateAddOn() throws Exception {

        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("Extra Sauce");
        request.setDescription("Special sauce");
        request.setPrice(new BigDecimal("20.00"));


        mockMvc.perform(
                        post("/api/v1/menu/add-ons")
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
                .andExpect(
                        jsonPath("$.name")
                                .value("Extra Sauce")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Special sauce")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(20.00)
                );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetAddOnById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/menu/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(addOn.getId().toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Additional cheese")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(30.00)
                );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllAddOns() throws Exception {

        mockMvc.perform(
                        get("/api/v1/menu/add-ons")
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
                                .value("Extra Cheese")
                );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateAddOn() throws Exception {

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("Extra Cheddar");
        request.setDescription("Extra cheddar cheese");
        request.setPrice(new BigDecimal("40.00"));


        mockMvc.perform(
                        patch(
                                "/api/v1/menu/add-ons/"
                                        + addOn.getId()
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
                                .value(addOn.getId().toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Extra Cheddar")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Extra cheddar cheese")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(40.00)
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteAddOn() throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/v1/menu/add-ons/"
                                        + addOn.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());


        AddOn deletedAddOn =
                addOnRepository
                        .findById(addOn.getId())
                        .orElseThrow();

        /*
         * AddOn uses soft delete.
         */

        assertThat(deletedAddOn.isActive())
                .isFalse();
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    @Test
    void shouldRejectCreateAddOnWithoutName()
            throws Exception {

        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("");
        request.setDescription("Invalid add-on");
        request.setPrice(new BigDecimal("20.00"));


        mockMvc.perform(
                        post("/api/v1/menu/add-ons")
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
    void shouldRejectCreateAddOnWithoutPrice()
            throws Exception {

        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("Extra Sauce");
        request.setDescription("Special sauce");
        request.setPrice(null);


        mockMvc.perform(
                        post("/api/v1/menu/add-ons")
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
    void shouldReturnNotFoundForUnknownAddOn()
            throws Exception {

        UUID unknownId = UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/menu/add-ons/"
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
    void shouldReturnNotFoundWhenDeletingUnknownAddOn()
            throws Exception {

        UUID unknownId = UUID.randomUUID();


        mockMvc.perform(
                        delete(
                                "/api/v1/menu/add-ons/"
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