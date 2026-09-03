package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;
import com.thewildchild.management.menu.service.MenuCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuCategoryService menuCategoryService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldCreateCategorySuccessfully() throws Exception {

        // Arrange
        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("Main Course");
        request.setDisplayOrder(1);

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        UUID categoryId = UUID.randomUUID();

        response.setId(categoryId);
        response.setName("Main Course");

        when(menuCategoryService.createCategory(
                any(CreateMenuCategoryRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(categoryId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Main Course")
                );

        // Verify
        verify(menuCategoryService)
                .createCategory(
                        any(CreateMenuCategoryRequest.class)
                );
    }


    @Test
    void shouldGetCategoryByIdSuccessfully() throws Exception {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        response.setId(categoryId);
        response.setName("Main Course");

        when(menuCategoryService.getCategoryById(categoryId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/categories/{categoryId}",
                                categoryId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(categoryId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Main Course")
                );

        // Verify
        verify(menuCategoryService)
                .getCategoryById(categoryId);
    }


    @Test
    void shouldGetAllCategoriesSuccessfully() throws Exception {

        // Arrange
        MenuCategoryResponse first =
                new MenuCategoryResponse();

        first.setId(UUID.randomUUID());
        first.setName("Main Course");

        MenuCategoryResponse second =
                new MenuCategoryResponse();

        second.setId(UUID.randomUUID());
        second.setName("Beverages");

        when(menuCategoryService.getAllCategories())
                .thenReturn(List.of(first, second));

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/categories")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Main Course")
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("Beverages")
                );

        // Verify
        verify(menuCategoryService)
                .getAllCategories();
    }


    @Test
    void shouldUpdateCategorySuccessfully() throws Exception {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("Starters");

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        response.setId(categoryId);
        response.setName("Starters");

        when(menuCategoryService.updateCategory(
                eq(categoryId),
                any(UpdateMenuCategoryRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        patch("/api/v1/menu/categories/{categoryId}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(categoryId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Starters")
                );

        // Verify
        verify(menuCategoryService)
                .updateCategory(
                        eq(categoryId),
                        any(UpdateMenuCategoryRequest.class)
                );
    }


    @Test
    void shouldDeleteCategorySuccessfully() throws Exception {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete("/api/v1/menu/categories/{categoryId}",
                                categoryId)
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(menuCategoryService)
                .deleteCategory(categoryId);
    }


    @Test
    void shouldRejectCreateCategoryWhenNameIsBlank()
            throws Exception {

        // Arrange
        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        // Verify
        verifyNoInteractions(menuCategoryService);
    }


//    @Test
//    void shouldRejectUpdateCategoryWhenRequestIsInvalid()
//            throws Exception {
//
//        // Arrange
//        UUID categoryId = UUID.randomUUID();
//
//        UpdateMenuCategoryRequest request =
//                new UpdateMenuCategoryRequest();
//
//        request.setName("");
//
//        // Act & Assert
//        mockMvc.perform(
//                        patch("/api/v1/menu/categories/{categoryId}",
//                                categoryId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(
//                                        objectMapper.writeValueAsString(request)
//                                )
//                )
//                .andExpect(status().isBadRequest());
//
//        // Verify
//        verifyNoInteractions(menuCategoryService);
//    }
}