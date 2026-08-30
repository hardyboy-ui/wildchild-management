package com.thewildchild.management.table.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.table.dto.request.CreateCafeTableRequest;
import com.thewildchild.management.table.dto.request.UpdateCafeTableRequest;
import com.thewildchild.management.table.dto.response.CafeTableResponse;
import com.thewildchild.management.table.service.CafeTableService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CafeTableController.class)
@AutoConfigureMockMvc(addFilters = false)
class CafeTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CafeTableService cafeTableService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldCreateTableSuccessfully() throws Exception {

        // Arrange
        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("T-01");
        request.setCapacity(4);
        request.setDisplayOrder(1);

        UUID tableId = UUID.randomUUID();

        CafeTableResponse response =
                new CafeTableResponse();

        response.setId(tableId);
        response.setTableNumber("T-01");
        response.setCapacity(4);
        response.setDisplayOrder(1);

        when(cafeTableService.createTable(
                any(CreateCafeTableRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/tables")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-01")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );

        // Verify
        verify(cafeTableService)
                .createTable(
                        any(CreateCafeTableRequest.class)
                );
    }


    @Test
    void shouldRejectCreateTableWhenRequestIsInvalid()
            throws Exception {

        // Arrange
        CreateCafeTableRequest request =
                new CreateCafeTableRequest();

        request.setTableNumber("");
        request.setCapacity(0);
        request.setDisplayOrder(0);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/tables")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldGetTableByIdSuccessfully() throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();

        CafeTableResponse response =
                new CafeTableResponse();

        response.setId(tableId);
        response.setTableNumber("T-01");
        response.setCapacity(4);
        response.setDisplayOrder(1);

        when(cafeTableService.getTableById(tableId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/tables/{tableId}", tableId)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-01")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );

        // Verify
        verify(cafeTableService)
                .getTableById(tableId);
    }


    @Test
    void shouldGetAllTablesSuccessfully() throws Exception {

        // Arrange
        CafeTableResponse response1 =
                new CafeTableResponse();

        response1.setId(UUID.randomUUID());
        response1.setTableNumber("T-01");
        response1.setCapacity(4);
        response1.setDisplayOrder(1);

        CafeTableResponse response2 =
                new CafeTableResponse();

        response2.setId(UUID.randomUUID());
        response2.setTableNumber("T-02");
        response2.setCapacity(6);
        response2.setDisplayOrder(2);

        when(cafeTableService.getAllTables())
                .thenReturn(List.of(response1, response2));

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/tables")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].tableNumber")
                                .value("T-01")
                )
                .andExpect(
                        jsonPath("$[0].capacity")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$[1].tableNumber")
                                .value("T-02")
                )
                .andExpect(
                        jsonPath("$[1].capacity")
                                .value(6)
                );

        // Verify
        verify(cafeTableService)
                .getAllTables();
    }


    @Test
    void shouldUpdateTableSuccessfully() throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber("T-01");
        request.setCapacity(6);
        request.setDisplayOrder(2);

        CafeTableResponse response =
                new CafeTableResponse();

        response.setId(tableId);
        response.setTableNumber("T-01");
        response.setCapacity(6);
        response.setDisplayOrder(2);

        when(cafeTableService.updateTable(
                eq(tableId),
                any(UpdateCafeTableRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        patch(
                                "/api/v1/tables/{tableId}",
                                tableId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.tableNumber")
                                .value("T-01")
                )
                .andExpect(
                        jsonPath("$.capacity")
                                .value(6)
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(2)
                );

        // Verify
        verify(cafeTableService)
                .updateTable(
                        eq(tableId),
                        any(UpdateCafeTableRequest.class)
                );
    }


    @Test
    void shouldRejectUpdateTableWhenRequestIsInvalid()
            throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();

        UpdateCafeTableRequest request =
                new UpdateCafeTableRequest();

        request.setTableNumber(
                "A".repeat(51)
        );

        // Act & Assert
        mockMvc.perform(
                        patch(
                                "/api/v1/tables/{tableId}",
                                tableId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldDeleteTableSuccessfully() throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete("/api/v1/tables/{tableId}", tableId)
                )
                .andExpect(status().isNoContent());

        // Verify
        verify(cafeTableService)
                .deleteTable(tableId);
    }
}