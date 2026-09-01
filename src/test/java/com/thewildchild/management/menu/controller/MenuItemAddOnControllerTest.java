package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.service.MenuItemAddOnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuItemAddOnController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemAddOnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuItemAddOnService menuItemAddOnService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldAddAddOnToMenuItemSuccessfully()
            throws Exception {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        post(
                                "/api/v1/menu/items/{menuItemId}/add-ons/{addOnId}",
                                menuItemId,
                                addOnId
                        )
                )
                .andExpect(status().isCreated());

        // Verify
        verify(menuItemAddOnService)
                .addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                );
    }


    @Test
    void shouldRemoveAddOnFromMenuItemSuccessfully()
            throws Exception {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete(
                                "/api/v1/menu/items/{menuItemId}/add-ons/{addOnId}",
                                menuItemId,
                                addOnId
                        )
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(menuItemAddOnService)
                .removeAddOnFromMenuItem(
                        menuItemId,
                        addOnId
                );
    }


    @Test
    void shouldGetAddOnsForMenuItemSuccessfully()
            throws Exception {

        // Arrange
        UUID menuItemId = UUID.randomUUID();

        UUID addOnId1 = UUID.randomUUID();
        UUID addOnId2 = UUID.randomUUID();

        AddOnResponse response1 =
                new AddOnResponse();

        response1.setId(addOnId1);
        response1.setName("Extra Cheese");
        response1.setPrice(
                new BigDecimal("50.00")
        );

        AddOnResponse response2 =
                new AddOnResponse();

        response2.setId(addOnId2);
        response2.setName("Extra Sauce");
        response2.setPrice(
                new BigDecimal("30.00")
        );

        when(menuItemAddOnService.getAddOnsForMenuItem(
                menuItemId
        )).thenReturn(
                List.of(response1, response2)
        );

        // Act & Assert
        mockMvc.perform(
                        get(
                                "/api/v1/menu/items/{menuItemId}/add-ons",
                                menuItemId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(addOnId1.toString())
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$[0].price")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(addOnId2.toString())
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("Extra Sauce")
                )
                .andExpect(
                        jsonPath("$[1].price")
                                .value(30.00)
                );

        // Verify
        verify(menuItemAddOnService)
                .getAddOnsForMenuItem(menuItemId);
    }
}