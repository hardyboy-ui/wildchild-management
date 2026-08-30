package com.thewildchild.management.diningsession.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewildchild.management.auth.security.JwtService;
import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.service.DiningSessionService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiningSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiningSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiningSessionService diningSessionService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void shouldOpenSessionSuccessfully() throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(tableId);

        DiningSessionResponse response =
                new DiningSessionResponse();

        response.setId(sessionId);
        response.setTableId(tableId);
        response.setStatus(DiningSessionStatus.OPEN);

        when(diningSessionService.openSession(
                any(CreateDiningSessionRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/dining-sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(sessionId.toString())
                )
                .andExpect(
                        jsonPath("$.tableId")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("OPEN")
                );

        // Verify
        verify(diningSessionService)
                .openSession(
                        any(CreateDiningSessionRequest.class)
                );
    }


    @Test
    void shouldRejectOpenSessionWhenTableIdIsMissing()
            throws Exception {

        // Arrange
        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        // tableId intentionally not set

        // Act & Assert
        mockMvc.perform(
                        post("/api/v1/dining-sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldGetSessionByIdSuccessfully()
            throws Exception {

        // Arrange
        UUID sessionId = UUID.randomUUID();
        UUID tableId = UUID.randomUUID();

        DiningSessionResponse response =
                new DiningSessionResponse();

        response.setId(sessionId);
        response.setTableId(tableId);
        response.setStatus(DiningSessionStatus.OPEN);

        when(diningSessionService.getSessionById(sessionId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/{sessionId}",
                                sessionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(sessionId.toString())
                )
                .andExpect(
                        jsonPath("$.tableId")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("OPEN")
                );

        // Verify
        verify(diningSessionService)
                .getSessionById(sessionId);
    }


    @Test
    void shouldGetOpenSessionByTableSuccessfully()
            throws Exception {

        // Arrange
        UUID tableId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        DiningSessionResponse response =
                new DiningSessionResponse();

        response.setId(sessionId);
        response.setTableId(tableId);
        response.setStatus(DiningSessionStatus.OPEN);

        when(diningSessionService.getOpenSessionByTable(tableId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get(
                                "/api/v1/dining-sessions/table/{tableId}/open",
                                tableId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(sessionId.toString())
                )
                .andExpect(
                        jsonPath("$.tableId")
                                .value(tableId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("OPEN")
                );

        // Verify
        verify(diningSessionService)
                .getOpenSessionByTable(tableId);
    }


    @Test
    void shouldGetOpenSessionsSuccessfully()
            throws Exception {

        // Arrange
        DiningSessionResponse response1 =
                new DiningSessionResponse();

        response1.setId(UUID.randomUUID());
        response1.setTableId(UUID.randomUUID());
        response1.setStatus(DiningSessionStatus.OPEN);

        DiningSessionResponse response2 =
                new DiningSessionResponse();

        response2.setId(UUID.randomUUID());
        response2.setTableId(UUID.randomUUID());
        response2.setStatus(DiningSessionStatus.OPEN);

        when(diningSessionService.getOpenSessions())
                .thenReturn(List.of(response1, response2));

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/dining-sessions/open")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(response1.getId().toString())
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(response2.getId().toString())
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("OPEN")
                );

        // Verify
        verify(diningSessionService)
                .getOpenSessions();
    }


    @Test
    void shouldCloseSessionSuccessfully()
            throws Exception {

        // Arrange
        UUID sessionId = UUID.randomUUID();
        UUID tableId = UUID.randomUUID();

        DiningSessionResponse response =
                new DiningSessionResponse();

        response.setId(sessionId);
        response.setTableId(tableId);
        response.setStatus(DiningSessionStatus.CLOSED);

        when(diningSessionService.closeSession(sessionId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post(
                                "/api/v1/dining-sessions/{sessionId}/close",
                                sessionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(sessionId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("CLOSED")
                );

        // Verify
        verify(diningSessionService)
                .closeSession(sessionId);
    }


    @Test
    void shouldCancelSessionSuccessfully()
            throws Exception {

        // Arrange
        UUID sessionId = UUID.randomUUID();
        UUID tableId = UUID.randomUUID();

        DiningSessionResponse response =
                new DiningSessionResponse();

        response.setId(sessionId);
        response.setTableId(tableId);
        response.setStatus(DiningSessionStatus.CANCELLED);

        when(diningSessionService.cancelSession(sessionId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post(
                                "/api/v1/dining-sessions/{sessionId}/cancel",
                                sessionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(sessionId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("CANCELLED")
                );

        // Verify
        verify(diningSessionService)
                .cancelSession(sessionId);
    }
}