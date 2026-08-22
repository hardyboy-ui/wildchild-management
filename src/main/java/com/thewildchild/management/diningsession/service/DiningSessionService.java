package com.thewildchild.management.diningsession.service;

import com.thewildchild.management.diningsession.dto.request.CreateDiningSessionRequest;
import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;

import java.util.List;
import java.util.UUID;

public interface DiningSessionService {

    DiningSessionResponse openSession(
            CreateDiningSessionRequest request
    );

    DiningSessionResponse getSessionById(UUID sessionId);

    DiningSessionResponse getOpenSessionByTable(UUID tableId);

    List<DiningSessionResponse> getOpenSessions();

    DiningSessionResponse closeSession(UUID sessionId);

    DiningSessionResponse cancelSession(UUID sessionId);
}