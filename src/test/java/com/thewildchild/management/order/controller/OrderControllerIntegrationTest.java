package com.thewildchild.management.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;


    private String jwtToken;

    private CafeTable cafeTable;

    private DiningSession diningSession;

    private MenuCategory menuCategory;

    private MenuItem menuItem;

    private Order order;


    @BeforeEach
    void setUp() throws Exception{

        /*
         * ---------------------------------------------------------
         * TEST USER
         * ---------------------------------------------------------
         */

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Owner");
        user.setEmail("order-test-owner@test.com");
        user.setPasswordHash(
                passwordEncoder.encode("Password@123")
        );
        user.setRole(Role.OWNER);
        user.setActive(true);

        user = userRepository.saveAndFlush(user);

        /*
         * ---------------------------------------------------------
         * LOGIN
         * ---------------------------------------------------------
         */

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setEmail("order-test-owner@test.com");
        loginRequest.setPassword("Password@123");


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
         * MENU CATEGORY
         * ---------------------------------------------------------
         */

        menuCategory = new MenuCategory();

        menuCategory.setName("Test Category");
        menuCategory.setDescription(
                "Category for integration testing"
        );
        menuCategory.setDisplayOrder(1);
        menuCategory.setActive(true);

        menuCategory =
                menuCategoryRepository.saveAndFlush(
                        menuCategory
                );


        /*
         * ---------------------------------------------------------
         * MENU ITEM
         * ---------------------------------------------------------
         */

        menuItem = new MenuItem();

        menuItem.setName("Test Burger");
        menuItem.setDescription(
                "Burger for integration testing"
        );
        menuItem.setPrice(
                new BigDecimal("100.00")
        );
        menuItem.setFoodType(FoodType.VEG);
        menuItem.setActive(true);
        menuItem.setCategory(menuCategory);

        menuItem =
                menuItemRepository.saveAndFlush(
                        menuItem
                );


        /*
         * ---------------------------------------------------------
         * TABLE
         * ---------------------------------------------------------
         */

        cafeTable = new CafeTable();

        cafeTable.setTableNumber("T-ORDER-TEST");
        cafeTable.setCapacity(4);
        cafeTable.setDisplayOrder(1);
        cafeTable.setActive(true);

        cafeTable =
                cafeTableRepository.saveAndFlush(
                        cafeTable
                );


        /*
         * ---------------------------------------------------------
         * DINING SESSION
         * ---------------------------------------------------------
         */

        diningSession = new DiningSession();

        diningSession.setSessionNumber(
                "DS-ORDER-TEST"
        );
        diningSession.setTable(cafeTable);
        diningSession.setStatus(
                DiningSessionStatus.OPEN
        );
        diningSession.setOpenedAt(LocalDateTime.now());

        diningSession =
                diningSessionRepository.saveAndFlush(
                        diningSession
                );


        /*
         * ---------------------------------------------------------
         * ORDER
         *
         * Used by get/add-item/close tests.
         * ---------------------------------------------------------
         */

        order = new Order();

        order.setOrderNumber("ORD-ORDER-TEST");
        order.setDiningSession(diningSession);
        order.setStatus(OrderStatus.OPEN);
        order.setBillingStatus(
                OrderBillingStatus.UNBILLED
        );

        order =
                orderRepository.saveAndFlush(order);
    }


    /*
     * =========================================================
     * CREATE ORDER
     * =========================================================
     */

    @Test
    void shouldCreateOrder() throws Exception {

        mockMvc.perform(
                        post("/api/v1/orders")
                                .param(
                                        "diningSessionId",
                                        diningSession.getId().toString()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.diningSessionId")
                                .value(
                                        diningSession.getId()
                                                .toString()
                                )
                );


        /*
         * Verify database state.
         */

        long orderCount =
                orderRepository.count();

        assertThat(orderCount)
                .isEqualTo(2);


        Order createdOrder =
                orderRepository
                        .findAll()
                        .stream()
                        .filter(savedOrder ->
                                !savedOrder.getId()
                                        .equals(order.getId())
                        )
                        .findFirst()
                        .orElseThrow();


        assertThat(createdOrder.getDiningSession().getId())
                .isEqualTo(diningSession.getId());

        assertThat(createdOrder.getStatus())
                .isEqualTo(OrderStatus.OPEN);

        assertThat(createdOrder.getBillingStatus())
                .isEqualTo(
                        OrderBillingStatus.UNBILLED
                );
    }


    /*
     * =========================================================
     * GET ORDER
     * =========================================================
     */

    @Test
    void shouldGetOrderById() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/orders/"
                                        + order.getId()
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
                                        order.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.orderNumber")
                                .value(
                                        "ORD-ORDER-TEST"
                                )
                )
                .andExpect(
                        jsonPath("$.diningSessionId")
                                .value(
                                        diningSession.getId()
                                                .toString()
                                )
                );
    }


    /*
     * =========================================================
     * ADD ORDER ITEM
     * =========================================================
     */

    @Test
    void shouldAddOrderItem() throws Exception {

        AddOrderItemRequest request =
                new AddOrderItemRequest();

        request.setMenuItemId(
                menuItem.getId()
        );

        request.setQuantity(2);

        request.setSpecialInstructions(
                "Extra spicy"
        );

        request.setAddOns(null);


        mockMvc.perform(
                        post(
                                "/api/v1/orders/"
                                        + order.getId()
                                        + "/items"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + jwtToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        order.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.items")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.items.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].menuItemId")
                                .value(
                                        menuItem.getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.items[0].menuItemName")
                                .value(
                                        "Test Burger"
                                )
                )
                .andExpect(
                        jsonPath("$.items[0].quantity")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.items[0].unitPrice")
                                .value(100.00)
                )
                .andExpect(
                        jsonPath("$.items[0].specialInstructions")
                                .value("Extra spicy")
                );


        /*
         * Verify database state.
         */

        Order updatedOrder =
                orderRepository
                        .findById(order.getId())
                        .orElseThrow();

        assertThat(updatedOrder.getItems())
                .hasSize(1);

        OrderItem orderItem =
                updatedOrder.getItems()
                        .get(0);

        assertThat(orderItem.getMenuItem().getId())
                .isEqualTo(menuItem.getId());

        assertThat(orderItem.getQuantity())
                .isEqualTo(2);

        assertThat(orderItem.getUnitPrice())
                .isEqualByComparingTo(
                        "100.00"
                );

        assertThat(
                orderItem.getKotSentQuantity()
        )
                .isEqualTo(0);
    }
}