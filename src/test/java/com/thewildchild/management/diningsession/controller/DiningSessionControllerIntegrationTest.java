package com.thewildchild.management.diningsession.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DiningSessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;


    private String jwtToken;

    private CafeTable cafeTable;

    private DiningSession diningSession;

    private Order order;


    /*
     * =========================================================
     * SETUP
     * =========================================================
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
         * TABLE
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


        /*
         * ---------------------------------------------------------
         * MENU CATEGORY
         * ---------------------------------------------------------
         */

        MenuCategory category = new MenuCategory();

        category.setName("Test Category");
        category.setDescription("Test category for integration tests");
        category.setDisplayOrder(1);
        category.setActive(true);

        category = menuCategoryRepository.saveAndFlush(category);


        /*
         * ---------------------------------------------------------
         * MENU ITEM
         * ---------------------------------------------------------
         */

        MenuItem menuItem = new MenuItem();

        menuItem.setName("Test Burger");
        menuItem.setDescription("Test burger");
        menuItem.setPrice(new BigDecimal("100.00"));
        menuItem.setActive(true);
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setCategory(category);

        menuItem = menuItemRepository.saveAndFlush(menuItem);
        /*
         * ---------------------------------------------------------
         * DINING SESSION
         * ---------------------------------------------------------
         */

        diningSession = new DiningSession();

        diningSession.setSessionNumber("DS-TEST");
        diningSession.setTable(cafeTable);
        diningSession.setStatus(DiningSessionStatus.OPEN);
        diningSession.setOpenedAt(LocalDateTime.now());

        diningSession = diningSessionRepository.saveAndFlush(diningSession);


        /*
         * ---------------------------------------------------------
         * ORDER
         * ---------------------------------------------------------
         */

        order = new Order();

        order.setOrderNumber("ORD-TEST");
        order.setDiningSession(diningSession);
        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);


        /*
         * IMPORTANT:
         * Maintain both sides of the relationship.
         */

        diningSession.getOrders().add(order);


        /*
         * ---------------------------------------------------------
         * ORDER ITEM
         * ---------------------------------------------------------
         */

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("100.00"));
        orderItem.setKotSentQuantity(2);

        order.getItems().add(orderItem);


        /*
         * ---------------------------------------------------------
         * SAVE ORDER
         * ---------------------------------------------------------
         */

        order = orderRepository.saveAndFlush(order);
    }


    /*
     * =========================================================
     * OPEN SESSION
     * =========================================================
     */

    @Test
    void shouldOpenDiningSession() throws Exception {

        /*
         * Create another table because the existing table
         * already has an open session.
         */

        CafeTable newTable = new CafeTable();

        newTable.setTableNumber("T-101");
        newTable.setCapacity(4);
        newTable.setDisplayOrder(2);
        newTable.setActive(true);

        newTable =
                cafeTableRepository.saveAndFlush(
                        newTable
                );


        String request = """
                {
                    "tableId": "%s"
                }
                """.formatted(
                newTable.getId()
        );


        mockMvc.perform(
                        post("/api/v1/dining-sessions")
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
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.tableId")
                                .value(
                                        newTable.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-101")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        DiningSessionStatus.OPEN
                                                .name()
                                )
                );
    }


    /*
     * =========================================================
     * GET SESSION BY ID
     * =========================================================
     */

    @Test
    void shouldGetDiningSessionById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/"
                                        + diningSession.getId()
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        diningSession.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sessionNumber")
                                .value("DS-TEST")
                )
                .andExpect(
                        jsonPath("$.tableId")
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
                        jsonPath("$.status")
                                .value(
                                        DiningSessionStatus.OPEN
                                                .name()
                                )
                );
    }


    /*
     * =========================================================
     * GET OPEN SESSION BY TABLE
     * =========================================================
     */

    @Test
    void shouldGetOpenSessionByTable() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/table/"
                                        + cafeTable.getId()
                                        + "/open"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        diningSession.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sessionNumber")
                                .value("DS-TEST")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        DiningSessionStatus.OPEN
                                                .name()
                                )
                );
    }


    /*
     * =========================================================
     * GET ALL OPEN SESSIONS
     * =========================================================
     */

    @Test
    void shouldGetOpenSessions() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/open"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(
                                        diningSession.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value(
                                        DiningSessionStatus.OPEN
                                                .name()
                                )
                );
    }




    /*
     * =========================================================
     * CANCEL SESSION
     * =========================================================
     */

    @Test
    void shouldCancelDiningSession() throws Exception {

        /*
         * Cancel uses a separate session so the close-session
         * test data does not interfere with this test.
         */

        CafeTable cancelTable = new CafeTable();

        cancelTable.setTableNumber("T-CANCEL");
        cancelTable.setCapacity(4);
        cancelTable.setDisplayOrder(3);
        cancelTable.setActive(true);

        cancelTable =
                cafeTableRepository.saveAndFlush(
                        cancelTable
                );


        DiningSession cancelSession =
                new DiningSession();

        cancelSession.setSessionNumber(
                "DS-CANCEL"
        );

        cancelSession.setTable(cancelTable);
        cancelSession.setOpenedAt(LocalDateTime.now());

        cancelSession.setStatus(
                DiningSessionStatus.OPEN
        );

        cancelSession =
                diningSessionRepository.saveAndFlush(
                        cancelSession
                );


        mockMvc.perform(
                        post(
                                "/api/v1/dining-sessions/"
                                        + cancelSession.getId()
                                        + "/cancel"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        cancelSession.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        DiningSessionStatus.CANCELLED
                                                .name()
                                )
                );


        /*
         * Verify database state.
         */

        DiningSession updatedSession =
                diningSessionRepository
                        .findById(
                                cancelSession.getId()
                        )
                        .orElseThrow();


        assertThat(
                updatedSession.getStatus()
        )
                .isEqualTo(
                        DiningSessionStatus.CANCELLED
                );
    }


    /*
     * =========================================================
     * VALIDATION
     * =========================================================
     */

    @Test
    void shouldRejectOpenSessionWithoutTableId()
            throws Exception {

        String request = """
                {
                    "tableId": null
                }
                """;


        mockMvc.perform(
                        post("/api/v1/dining-sessions")
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
     * UNAUTHORIZED
     * =========================================================
     */

    @Test
    void shouldRejectRequestWithoutJwt()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/"
                                        + diningSession.getId()
                        )
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
    void shouldReturnNotFoundForUnknownSession()
            throws Exception {

        UUID randomId =
                UUID.randomUUID();


        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/"
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