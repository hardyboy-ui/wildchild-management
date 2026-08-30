package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;
import com.thewildchild.management.menu.entity.FoodType;
import com.thewildchild.management.menu.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MenuItemController.class
)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuItemService menuItemService;

    @MockitoBean
    private JwtService jwtService;



    @Test
    void shouldCreateMenuItemSuccessfully() throws Exception {

        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setName("Paneer Tikka");
        request.setDescription("Grilled paneer");
        request.setPrice(new BigDecimal("250.00"));
        request.setImageUrl("https://example.com/paneer.jpg");
        request.setFoodType(FoodType.VEG);
        request.setCategoryId(categoryId);

        MenuItemResponse response =
                new MenuItemResponse();

        response.setId(itemId);
        response.setName("Paneer Tikka");
        response.setDescription("Grilled paneer");
        response.setPrice(new BigDecimal("250.00"));
        response.setImageUrl("https://example.com/paneer.jpg");
        response.setFoodType(FoodType.VEG);
        response.setCategoryId(categoryId);

        when(menuItemService.createMenuItem(
                any(CreateMenuItemRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(itemId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Paneer Tikka")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(250.00)
                )
                .andExpect(
                        jsonPath("$.foodType")
                                .value("VEG")
                )
                .andExpect(
                        jsonPath("$.categoryId")
                                .value(categoryId.toString())
                );

        // Verify
        verify(menuItemService)
                .createMenuItem(
                        any(CreateMenuItemRequest.class)
                );
    }


    @Test
    void shouldGetMenuItemByIdSuccessfully() throws Exception {

        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        MenuItemResponse response =
                new MenuItemResponse();

        response.setId(itemId);
        response.setName("Paneer Tikka");
        response.setPrice(new BigDecimal("250.00"));
        response.setFoodType(FoodType.VEG);
        response.setCategoryId(categoryId);

        when(menuItemService.getMenuItemById(itemId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/items/{itemId}", itemId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(itemId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Paneer Tikka")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(250.00)
                )
                .andExpect(
                        jsonPath("$.foodType")
                                .value("VEG")
                );

        // Verify
        verify(menuItemService)
                .getMenuItemById(itemId);
    }


    @Test
    void shouldGetAllMenuItemsSuccessfully() throws Exception {

        // Arrange
        UUID itemId1 = UUID.randomUUID();
        UUID itemId2 = UUID.randomUUID();

        MenuItemResponse item1 =
                new MenuItemResponse();

        item1.setId(itemId1);
        item1.setName("Paneer Tikka");
        item1.setPrice(new BigDecimal("250.00"));
        item1.setFoodType(FoodType.VEG);

        MenuItemResponse item2 =
                new MenuItemResponse();

        item2.setId(itemId2);
        item2.setName("Chicken Tikka");
        item2.setPrice(new BigDecimal("320.00"));
        item2.setFoodType(FoodType.NON_VEG);

        when(menuItemService.getAllMenuItems())
                .thenReturn(List.of(item1, item2));

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/items")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(itemId1.toString())
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Paneer Tikka")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(itemId2.toString())
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("Chicken Tikka")
                );

        // Verify
        verify(menuItemService)
                .getAllMenuItems();
    }


    @Test
    void shouldUpdateMenuItemSuccessfully() throws Exception {

        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName("Updated Paneer Tikka");
        request.setPrice(new BigDecimal("280.00"));
        request.setFoodType(FoodType.VEG);
        request.setCategoryId(categoryId);

        MenuItemResponse response =
                new MenuItemResponse();

        response.setId(itemId);
        response.setName("Updated Paneer Tikka");
        response.setPrice(new BigDecimal("280.00"));
        response.setFoodType(FoodType.VEG);
        response.setCategoryId(categoryId);

        when(menuItemService.updateMenuItem(
                eq(itemId),
                any(UpdateMenuItemRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        patch("/api/v1/menu/items/{itemId}", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(itemId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Updated Paneer Tikka")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(280.00)
                )
                .andExpect(
                        jsonPath("$.foodType")
                                .value("VEG")
                );

        // Verify
        verify(menuItemService)
                .updateMenuItem(
                        eq(itemId),
                        any(UpdateMenuItemRequest.class)
                );
    }


    @Test
    void shouldDeleteMenuItemSuccessfully() throws Exception {

        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete("/api/v1/menu/items/{itemId}", itemId)
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(menuItemService)
                .deleteMenuItem(itemId);
    }


    @Test
    void shouldRejectCreateMenuItemWhenRequestIsInvalid()
            throws Exception {

        // Arrange
        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }
}