package com.thewildchild.management.diningsession.service.mapper;

import com.thewildchild.management.diningsession.dto.response.DiningSessionResponse;
import com.thewildchild.management.diningsession.entity.DiningSession;
import org.springframework.stereotype.Component;

@Component
public class DiningSessionMapper {

    public DiningSessionResponse toResponse(DiningSession session) {

        DiningSessionResponse response = new DiningSessionResponse();

        response.setId(session.getId());
        response.setSessionNumber(session.getSessionNumber());
        response.setStatus(session.getStatus());
        response.setOpenedAt(session.getOpenedAt());
        response.setClosedAt(session.getClosedAt());

        if (session.getTable() != null) {
            response.setTableId(session.getTable().getId());
            response.setTableNumber(session.getTable().getTableNumber());
        }

        return response;
    }
}