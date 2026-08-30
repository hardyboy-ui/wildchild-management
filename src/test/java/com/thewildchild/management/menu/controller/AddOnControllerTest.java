package com.thewildchild.management.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.service.AddOnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
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
        controllers = AddOnController.class
)
@AutoConfigureMockMvc(addFilters = false)
class AddOnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddOnService addOnService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldCreateAddOnSuccessfully() throws Exception {

        // Arrange
        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("Extra Cheese");
        request.setDescription("Additional cheese");
        request.setPrice(new BigDecimal("50.00"));

        AddOnResponse response =
                new AddOnResponse();

        UUID addOnId = UUID.randomUUID();

        response.setId(addOnId);
        response.setName("Extra Cheese");
        response.setDescription("Additional cheese");
        response.setPrice(new BigDecimal("50.00"));

        when(addOnService.createAddOn(
                any(CreateAddOnRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/add-ons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(addOnId.toString())
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
                                .value(50.00)
                );

        // Verify
        verify(addOnService)
                .createAddOn(
                        any(CreateAddOnRequest.class)
                );
    }


    @Test
    void shouldRejectCreateAddOnWhenRequestIsInvalid()
            throws Exception {

        // Arrange
        CreateAddOnRequest request =
                new CreateAddOnRequest();

        // Missing required fields
        request.setName("");
        request.setDescription("Additional cheese");

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/menu/add-ons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldGetAddOnByIdSuccessfully() throws Exception {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOnResponse response =
                new AddOnResponse();

        response.setId(addOnId);
        response.setName("Extra Cheese");
        response.setPrice(new BigDecimal("50.00"));

        when(addOnService.getAddOnById(addOnId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/add-ons/{addOnId}",
                                addOnId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(addOnId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(50.00)
                );

        // Verify
        verify(addOnService)
                .getAddOnById(addOnId);
    }


    @Test
    void shouldGetAllAddOnsSuccessfully() throws Exception {

        // Arrange
        AddOnResponse response1 =
                new AddOnResponse();

        response1.setId(UUID.randomUUID());
        response1.setName("Extra Cheese");
        response1.setPrice(new BigDecimal("50.00"));

        AddOnResponse response2 =
                new AddOnResponse();

        response2.setId(UUID.randomUUID());
        response2.setName("Extra Sauce");
        response2.setPrice(new BigDecimal("30.00"));

        when(addOnService.getAllAddOns())
                .thenReturn(List.of(response1, response2));

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/menu/add-ons")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("Extra Sauce")
                );

        // Verify
        verify(addOnService)
                .getAllAddOns();
    }


    @Test
    void shouldUpdateAddOnSuccessfully() throws Exception {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("Extra Cheese");
        request.setDescription("Updated description");
        request.setPrice(new BigDecimal("60.00"));

        AddOnResponse response =
                new AddOnResponse();

        response.setId(addOnId);
        response.setName("Extra Cheese");
        response.setDescription("Updated description");
        response.setPrice(new BigDecimal("60.00"));

        when(addOnService.updateAddOn(
                eq(addOnId),
                any(UpdateAddOnRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        patch("/api/v1/menu/add-ons/{addOnId}",
                                addOnId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(addOnId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Extra Cheese")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Updated description")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(60.00)
                );

        // Verify
        verify(addOnService)
                .updateAddOn(
                        eq(addOnId),
                        any(UpdateAddOnRequest.class)
                );
    }


    @Test
    void shouldDeleteAddOnSuccessfully() throws Exception {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete("/api/v1/menu/add-ons/{addOnId}",
                                addOnId)
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(addOnService)
                .deleteAddOn(addOnId);
    }
}