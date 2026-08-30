package com.thewildchild.management.diningsession.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.diningsession.service.mapper.DiningSessionMapper;
import com.thewildchild.management.diningsession.service.validator.DiningSessionValidator;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.order.service.validator.OrderValidator;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiningSessionServiceImplTest {

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @Mock
    private CafeTableRepository cafeTableRepository;

    @Mock
    private DiningSessionMapper diningSessionMapper;

    @Mock
    private DiningSessionValidator diningSessionValidator;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderValidator orderValidator;

    @InjectMocks
    private DiningSessionServiceImpl diningSessionService;


    // =========================================================
    // OPEN SESSION
    // =========================================================

    @Test
    void shouldOpenSessionSuccessfully() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(tableId);

        CafeTable table =
                new CafeTable();

        table.setId(tableId);
        table.setActive(true);

        DiningSession savedSession =
                new DiningSession();

        DiningSessionResponse response =
                new DiningSessionResponse();

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        when(diningSessionRepository.save(any(DiningSession.class)))
                .thenReturn(savedSession);

        when(diningSessionMapper.toResponse(savedSession))
                .thenReturn(response);

        // Act
        DiningSessionResponse result =
                diningSessionService.openSession(request);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(diningSessionValidator)
                .validateCreate(tableId);

        verify(diningSessionRepository)
                .save(any(DiningSession.class));

        verify(diningSessionMapper)
                .toResponse(savedSession);
    }


    @Test
    void shouldThrowExceptionWhenOpeningSessionForNonExistingTable() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(tableId);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.openSession(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Active cafe table not found with id: "
                                + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(diningSessionValidator, never())
                .validateCreate(any(UUID.class));

        verify(diningSessionRepository, never())
                .save(any(DiningSession.class));
    }


    @Test
    void shouldThrowExceptionWhenOpeningSessionForInactiveTable() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(tableId);

        CafeTable table =
                new CafeTable();

        table.setId(tableId);
        table.setActive(false);

        when(cafeTableRepository.findById(tableId))
                .thenReturn(Optional.of(table));

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.openSession(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Active cafe table not found with id: "
                                + tableId
                );

        // Verify
        verify(cafeTableRepository)
                .findById(tableId);

        verify(diningSessionValidator, never())
                .validateCreate(any(UUID.class));

        verify(diningSessionRepository, never())
                .save(any(DiningSession.class));
    }


    // =========================================================
    // GET SESSION BY ID
    // =========================================================

    @Test
    void shouldGetSessionByIdSuccessfully() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        DiningSessionResponse response =
                new DiningSessionResponse();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(diningSessionMapper.toResponse(session))
                .thenReturn(response);

        // Act
        DiningSessionResponse result =
                diningSessionService.getSessionById(sessionId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(diningSessionMapper)
                .toResponse(session);
    }


    @Test
    void shouldThrowExceptionWhenSessionDoesNotExist() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.getSessionById(sessionId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Dining session not found with id: "
                                + sessionId
                );

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(diningSessionMapper, never())
                .toResponse(any(DiningSession.class));
    }


    // =========================================================
    // GET OPEN SESSION BY TABLE
    // =========================================================

    @Test
    void shouldGetOpenSessionByTableSuccessfully() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        DiningSessionResponse response =
                new DiningSessionResponse();

        when(diningSessionRepository
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                ))
                .thenReturn(Optional.of(session));

        when(diningSessionMapper.toResponse(session))
                .thenReturn(response);

        // Act
        DiningSessionResponse result =
                diningSessionService.getOpenSessionByTable(
                        tableId
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(diningSessionRepository)
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                );

        verify(diningSessionMapper)
                .toResponse(session);
    }


    @Test
    void shouldThrowExceptionWhenOpenSessionDoesNotExistForTable() {

        // Arrange
        UUID tableId = UUID.randomUUID();

        when(diningSessionRepository
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                ))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.getOpenSessionByTable(
                        tableId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "No open dining session found for table: "
                                + tableId
                );

        // Verify
        verify(diningSessionRepository)
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                );

        verify(diningSessionMapper, never())
                .toResponse(any(DiningSession.class));
    }


    // =========================================================
    // GET OPEN SESSIONS
    // =========================================================

    @Test
    void shouldGetAllOpenSessionsSuccessfully() {

        // Arrange
        DiningSession session1 =
                new DiningSession();

        DiningSession session2 =
                new DiningSession();

        DiningSessionResponse response1 =
                new DiningSessionResponse();

        DiningSessionResponse response2 =
                new DiningSessionResponse();

        when(diningSessionRepository
                .findAllByStatus(DiningSessionStatus.OPEN))
                .thenReturn(List.of(session1, session2));

        when(diningSessionMapper.toResponse(session1))
                .thenReturn(response1);

        when(diningSessionMapper.toResponse(session2))
                .thenReturn(response2);

        // Act
        List<DiningSessionResponse> result =
                diningSessionService.getOpenSessions();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(
                        response1,
                        response2
                );

        // Verify
        verify(diningSessionRepository)
                .findAllByStatus(DiningSessionStatus.OPEN);

        verify(diningSessionMapper)
                .toResponse(session1);

        verify(diningSessionMapper)
                .toResponse(session2);
    }


    // =========================================================
    // CLOSE SESSION
    // =========================================================

    @Test
    void shouldCloseSessionSuccessfully() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setStatus(DiningSessionStatus.OPEN);

        Order openOrder =
                new Order();

        openOrder.setStatus(OrderStatus.OPEN);

        Order closedOrder =
                new Order();

        closedOrder.setStatus(OrderStatus.CLOSED);

        List<Order> orders =
                List.of(openOrder, closedOrder);

        DiningSession savedSession =
                new DiningSession();

        savedSession.setStatus(DiningSessionStatus.CLOSED);

        DiningSessionResponse response =
                new DiningSessionResponse();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(orderRepository.findAllByDiningSessionId(sessionId))
                .thenReturn(orders);

        when(diningSessionRepository.save(session))
                .thenReturn(savedSession);

        when(diningSessionMapper.toResponse(savedSession))
                .thenReturn(response);

        // Act
        DiningSessionResponse result =
                diningSessionService.closeSession(sessionId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(session.getStatus())
                .isEqualTo(DiningSessionStatus.CLOSED);

        assertThat(openOrder.getStatus())
                .isEqualTo(OrderStatus.CLOSED);

        assertThat(closedOrder.getStatus())
                .isEqualTo(OrderStatus.CLOSED);

        assertThat(session.getClosedAt())
                .isNotNull();

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(orderRepository)
                .findAllByDiningSessionId(sessionId);

        verify(diningSessionValidator)
                .validateOrdersCanBeClosed(orders);

        verify(diningSessionRepository)
                .save(session);

        verify(diningSessionMapper)
                .toResponse(savedSession);
    }


    @Test
    void shouldNotCloseSessionWhenSessionIsNotOpen() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setStatus(DiningSessionStatus.CLOSED);

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.closeSession(sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Dining session is not open"
                );

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(orderRepository, never())
                .findAllByDiningSessionId(any(UUID.class));

        verify(diningSessionValidator, never())
                .validateOrdersCanBeClosed(anyList());

        verify(diningSessionRepository, never())
                .save(any(DiningSession.class));
    }


    @Test
    void shouldNotCloseSessionWhenOrdersCannotBeClosed() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setStatus(DiningSessionStatus.OPEN);

        List<Order> orders =
                List.of(new Order());

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(orderRepository.findAllByDiningSessionId(sessionId))
                .thenReturn(orders);

        doThrow(new BusinessException(
                "Orders cannot be closed"
        ))
                .when(diningSessionValidator)
                .validateOrdersCanBeClosed(orders);

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.closeSession(sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Orders cannot be closed"
                );

        // Assert
        assertThat(session.getStatus())
                .isEqualTo(DiningSessionStatus.OPEN);

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(orderRepository)
                .findAllByDiningSessionId(sessionId);

        verify(diningSessionValidator)
                .validateOrdersCanBeClosed(orders);

        verify(diningSessionRepository, never())
                .save(any(DiningSession.class));
    }


    // =========================================================
    // CANCEL SESSION
    // =========================================================

    @Test
    void shouldCancelSessionSuccessfully() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setStatus(DiningSessionStatus.OPEN);

        DiningSessionResponse response =
                new DiningSessionResponse();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(diningSessionMapper.toResponse(session))
                .thenReturn(response);

        // Act
        DiningSessionResponse result =
                diningSessionService.cancelSession(sessionId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(session.getStatus())
                .isEqualTo(DiningSessionStatus.CANCELLED);

        assertThat(session.getClosedAt())
                .isNotNull();

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(diningSessionMapper)
                .toResponse(session);

        verify(diningSessionRepository, never())
                .save(any(DiningSession.class));
    }


    @Test
    void shouldNotCancelSessionWhenSessionIsNotOpen() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        DiningSession session =
                new DiningSession();

        session.setStatus(DiningSessionStatus.CLOSED);

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.cancelSession(sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Dining session is not open"
                );

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(diningSessionMapper, never())
                .toResponse(any(DiningSession.class));
    }


    @Test
    void shouldNotCancelSessionWhenSessionDoesNotExist() {

        // Arrange
        UUID sessionId = UUID.randomUUID();

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                diningSessionService.cancelSession(sessionId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Dining session not found with id: "
                                + sessionId
                );

        // Verify
        verify(diningSessionRepository)
                .findById(sessionId);

        verify(diningSessionMapper, never())
                .toResponse(any(DiningSession.class));
    }
}