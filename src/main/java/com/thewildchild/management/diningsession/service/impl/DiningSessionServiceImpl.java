package com.thewildchild.management.diningsession.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.diningsession.service.DiningSessionService;
import com.thewildchild.management.diningsession.service.mapper.DiningSessionMapper;
import com.thewildchild.management.diningsession.service.validator.DiningSessionValidator;
import com.thewildchild.management.table.entity.CafeTable;
import com.thewildchild.management.table.repository.CafeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DiningSessionServiceImpl implements DiningSessionService {

    private final DiningSessionRepository diningSessionRepository;
    private final CafeTableRepository cafeTableRepository;
    private final DiningSessionMapper diningSessionMapper;
    private final DiningSessionValidator diningSessionValidator;

    @Override
    public DiningSessionResponse openSession(
            CreateDiningSessionRequest request
    ) {
        CafeTable table = findActiveTable(request.getTableId());

        diningSessionValidator.validateCreate(table.getId());

        DiningSession session = new DiningSession();

        session.setSessionNumber(generateSessionNumber());
        session.setTable(table);
        session.setStatus(DiningSessionStatus.OPEN);
        session.setOpenedAt(LocalDateTime.now());

        DiningSession savedSession =
                diningSessionRepository.save(session);

        return diningSessionMapper.toResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public DiningSessionResponse getSessionById(UUID sessionId) {

        DiningSession session = findSessionById(sessionId);

        return diningSessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public DiningSessionResponse getOpenSessionByTable(
            UUID tableId
    ) {
        DiningSession session = diningSessionRepository
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.OPEN
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No open dining session found for table: "
                                        + tableId
                        )
                );

        return diningSessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiningSessionResponse> getOpenSessions() {

        return diningSessionRepository
                .findAllByStatus(DiningSessionStatus.OPEN)
                .stream()
                .map(diningSessionMapper::toResponse)
                .toList();
    }

    @Override
    public DiningSessionResponse closeSession(UUID sessionId) {

        DiningSession session = findSessionById(sessionId);

        validateOpenSession(session);

        session.setStatus(DiningSessionStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());

        return diningSessionMapper.toResponse(session);
    }

    @Override
    public DiningSessionResponse cancelSession(UUID sessionId) {

        DiningSession session = findSessionById(sessionId);

        validateOpenSession(session);

        session.setStatus(DiningSessionStatus.CANCELLED);
        session.setClosedAt(LocalDateTime.now());

        return diningSessionMapper.toResponse(session);
    }

    private DiningSession findSessionById(UUID sessionId) {

        return diningSessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Dining session not found with id: "
                                        + sessionId
                        )
                );
    }

    private CafeTable findActiveTable(UUID tableId) {

        return cafeTableRepository.findById(tableId)
                .filter(CafeTable::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active cafe table not found with id: "
                                        + tableId
                        )
                );
    }

    private void validateOpenSession(DiningSession session) {

        if (session.getStatus() != DiningSessionStatus.OPEN) {
            throw new BusinessException(
                    "Dining session is not open"
            );
        }
    }

    private String generateSessionNumber() {

        return "DS-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}