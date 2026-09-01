package com.thewildchild.management.diningsession.service;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.diningsession.service.impl.DiningSessionServiceImpl;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderStatus;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("test")
@Transactional
class DiningSessionServiceIntegrationTest {

    @Autowired
    private DiningSessionServiceImpl diningSessionService;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private CafeTableRepository cafeTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        diningSessionRepository.deleteAll();
        cafeTableRepository.deleteAll();
    }

    @Test
    void shouldOpenDiningSession() {

        CafeTable table = createTable();

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(table.getId());

        DiningSessionResponse response =
                diningSessionService.openSession(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getSessionNumber()).isNotBlank();
        assertThat(response.getStatus())
                .isEqualTo(DiningSessionStatus.OPEN);
        assertThat(response.getOpenedAt()).isNotNull();

        DiningSession savedSession =
                diningSessionRepository
                        .findById(response.getId())
                        .orElseThrow();

        assertThat(savedSession.getTable().getId())
                .isEqualTo(table.getId());

        assertThat(savedSession.getStatus())
                .isEqualTo(DiningSessionStatus.OPEN);
    }

    @Test
    void shouldGetSessionById() {

        CafeTable table = createTable();

        DiningSession session = createOpenSession(table);

        DiningSessionResponse response =
                diningSessionService.getSessionById(
                        session.getId()
                );

        assertThat(response.getId())
                .isEqualTo(session.getId());

        assertThat(response.getSessionNumber())
                .isEqualTo(session.getSessionNumber());

        assertThat(response.getStatus())
                .isEqualTo(DiningSessionStatus.OPEN);
    }

    @Test
    void shouldGetOpenSessionByTable() {

        CafeTable table = createTable();

        DiningSession session =
                createOpenSession(table);

        DiningSessionResponse response =
                diningSessionService.getOpenSessionByTable(
                        table.getId()
                );

        assertThat(response.getId())
                .isEqualTo(session.getId());

        assertThat(response.getStatus())
                .isEqualTo(DiningSessionStatus.OPEN);
    }

    @Test
    void shouldGetAllOpenSessions() {

        CafeTable table1 = createTable("T1");
        CafeTable table2 = createTable("T2");

        createOpenSession(table1);
        createOpenSession(table2);

        DiningSession closedSession =
                createOpenSession(createTable("T3"));

        closedSession.setStatus(DiningSessionStatus.CLOSED);

        diningSessionRepository.save(closedSession);

        var sessions =
                diningSessionService.getOpenSessions();

        assertThat(sessions)
                .hasSize(2);

        assertThat(sessions)
                .allMatch(session ->
                        session.getStatus()
                                == DiningSessionStatus.OPEN);
    }

    @Test
    void shouldCloseSessionAndOpenOrders() {

        CafeTable table = createTable();

        DiningSession session =
                createOpenSession(table);

        Order openOrder = createOrder(
                session,
                OrderStatus.OPEN,
                OrderBillingStatus.PAID
        );

        DiningSessionResponse response =
                diningSessionService.closeSession(
                        session.getId()
                );

        assertThat(response.getStatus())
                .isEqualTo(DiningSessionStatus.CLOSED);

        assertThat(response.getClosedAt())
                .isNotNull();

        DiningSession savedSession =
                diningSessionRepository
                        .findById(session.getId())
                        .orElseThrow();

        assertThat(savedSession.getStatus())
                .isEqualTo(DiningSessionStatus.CLOSED);

        Order savedOrder =
                orderRepository
                        .findById(openOrder.getId())
                        .orElseThrow();

        assertThat(savedOrder.getStatus())
                .isEqualTo(OrderStatus.CLOSED);
    }

    @Test
    void shouldCancelOpenSession() {

        CafeTable table = createTable();

        DiningSession session =
                createOpenSession(table);

        DiningSessionResponse response =
                diningSessionService.cancelSession(
                        session.getId()
                );

        assertThat(response.getStatus())
                .isEqualTo(DiningSessionStatus.CANCELLED);

        assertThat(response.getClosedAt())
                .isNotNull();

        DiningSession savedSession =
                diningSessionRepository
                        .findById(session.getId())
                        .orElseThrow();

        assertThat(savedSession.getStatus())
                .isEqualTo(DiningSessionStatus.CANCELLED);
    }

    @Test
    void shouldNotGetInactiveTableSession() {

        CafeTable table = createTable();

        table.setActive(false);

        cafeTableRepository.save(table);

        CreateDiningSessionRequest request =
                new CreateDiningSessionRequest();

        request.setTableId(table.getId());

        assertThatThrownBy(() ->
                diningSessionService.openSession(request)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldNotCloseAlreadyClosedSession() {

        CafeTable table = createTable();

        DiningSession session =
                createOpenSession(table);

        session.setStatus(DiningSessionStatus.CLOSED);

        diningSessionRepository.save(session);

        assertThatThrownBy(() ->
                diningSessionService.closeSession(
                        session.getId()
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Dining session is not open");
    }

    @Test
    void shouldNotCancelAlreadyClosedSession() {

        CafeTable table = createTable();

        DiningSession session =
                createOpenSession(table);

        session.setStatus(DiningSessionStatus.CLOSED);

        diningSessionRepository.save(session);

        assertThatThrownBy(() ->
                diningSessionService.cancelSession(
                        session.getId()
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Dining session is not open");
    }

    @Test
    void shouldThrowWhenSessionDoesNotExist() {

        UUID sessionId = UUID.randomUUID();

        assertThatThrownBy(() ->
                diningSessionService.getSessionById(sessionId)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------------------------------------------------------
    // Test helpers
    // ---------------------------------------------------------

    private CafeTable createTable() {
        return createTable(
                "T-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );
    }

    private CafeTable createTable(String tableNumber) {

        CafeTable table = new CafeTable();

        table.setTableNumber(tableNumber);
        table.setCapacity(4);
        table.setDisplayOrder(1);
        table.setActive(true);

        return cafeTableRepository.save(table);
    }

    private DiningSession createOpenSession(
            CafeTable table
    ) {

        DiningSession session =
                new DiningSession();

        session.setSessionNumber(
                "DS-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        session.setTable(table);
        session.setStatus(DiningSessionStatus.OPEN);
        session.setOpenedAt(
                java.time.LocalDateTime.now()
        );

        return diningSessionRepository.save(session);
    }

    private Order createOrder(
            DiningSession session,
            OrderStatus status,
            OrderBillingStatus orderBillingStatus
    ) {

        Order order = new Order();

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        order.setDiningSession(session);
        order.setStatus(status);
        order.setBillingStatus(orderBillingStatus);

        return orderRepository.save(order);
    }
}