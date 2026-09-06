package com.thewildchild.management.table.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;

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
class CafeTableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String jwtToken;

    private CafeTable cafeTable;


    /*
     * ---------------------------------------------------------
     * TEST SETUP
     * ---------------------------------------------------------
     */

    @BeforeEach
    void setUp() throws Exception {

        /*
         * ---------------------------------------------------------
         * TEST USER
         * ---------------------------------------------------------
         */

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Owner");
        user.setEmail("owner@test.com");
        user.setPhoneNumber("9876543210");

        user.setPasswordHash(
                passwordEncoder.encode("password123")
        );

        user.setRole(Role.OWNER);
        user.setActive(true);

        userRepository.saveAndFlush(user);


        /*
         * ---------------------------------------------------------
         * LOGIN
         * ---------------------------------------------------------
         */

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setEmail("owner@test.com");
        loginRequest.setPassword("password123");


        String loginResponse =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        loginRequest
                                                )
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        JsonNode jsonNode =
                objectMapper.readTree(loginResponse);

        jwtToken =
                jsonNode.get("accessToken")
                        .asText();


        /*
         * ---------------------------------------------------------
         * TEST TABLE
         * ---------------------------------------------------------
         */

        cafeTable = new CafeTable();

        cafeTable.setTableNumber("T-TEST");
        cafeTable.setCapacity(4);
        cafeTable.setDisplayOrder(1);
        cafeTable.setActive(true);

        cafeTable =
                cafeTableRepository.saveAndFlush(
                        cafeTable
                );
    }


    /*
     * =========================================================
     * CREATE
     * =========================================================
     */

    @Test
    void shouldCreateTable() throws Exception {

        String request = """
                {
                    "tableNumber": "T-101",
                    "capacity": 4,
                    "displayOrder": 1
                }
                """;


        mockMvc.perform(
                        post("/api/v1/tables")
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-101")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );
    }


    /*
     * =========================================================
     * GET BY ID
     * =========================================================
     */

    @Test
    void shouldGetTableById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/tables/"
                                        + cafeTable.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        cafeTable.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-TEST")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );
    }


    /*
     * =========================================================
     * GET ALL
     * =========================================================
     */

    @Test
    void shouldGetAllTables() throws Exception {

        mockMvc.perform(
                        get("/api/v1/tables")
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[0].tableNumber")
                                .value("T-TEST")
                );
    }


    /*
     * =========================================================
     * UPDATE
     * =========================================================
     */

    @Test
    void shouldUpdateTable() throws Exception {

        String request = """
                {
                    "tableNumber": "T-UPDATED",
                    "capacity": 6,
                    "displayOrder": 2
                }
                """;


        mockMvc.perform(
                        patch(
                                "/api/v1/tables/"
                                        + cafeTable.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-UPDATED")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(6)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(2)
                );


        /*
         * Verify database state.
         */

        CafeTable updatedTable =
                cafeTableRepository
                        .findById(cafeTable.getId())
                        .orElseThrow();


        assertThat(updatedTable.getTableNumber())
                .isEqualTo("T-UPDATED");

        assertThat(updatedTable.getCapacity())
                .isEqualTo(6);

        assertThat(updatedTable.getDisplayOrder())
                .isEqualTo(2);
    }


    /*
     * =========================================================
     * DELETE
     * =========================================================
     */

    @Test
    void shouldDeleteTable() throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/v1/tables/"
                                        + cafeTable.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isNoContent()
                );


        /*
         * Verify database state.
         */

        CafeTable deletedTable =
                cafeTableRepository
                        .findById(cafeTable.getId())
                        .orElseThrow();

        assertThat(deletedTable.isActive())
                .isFalse();
    }


    /*
     * =========================================================
     * VALIDATION
     * =========================================================
     */

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        String request = """
                {
                    "tableNumber": "",
                    "capacity": 0,
                    "displayOrder": 0
                }
                """;


        mockMvc.perform(
                        post("/api/v1/tables")
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }


    /*
     * =========================================================
     * UNAUTHORIZED REQUEST
     * =========================================================
     */

    @Test
    void shouldRejectRequestWithoutJwt() throws Exception {

        mockMvc.perform(
                        get("/api/v1/tables")
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    /*
     * =========================================================
     * NOT FOUND
     * =========================================================
     */

    @Test
    void shouldReturnNotFoundForUnknownTable() throws Exception {

        UUID randomId = UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/tables/"
                                        + randomId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }
}