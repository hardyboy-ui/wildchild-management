package com.thewildchild.management.diningsession.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DiningSessionValidator {

    private final DiningSessionRepository diningSessionRepository;

    public void validateCreate(UUID tableId) {

        if (diningSessionRepository.existsByTableIdAndStatus(
                tableId,
                DiningSessionStatus.OPEN
        )) {
            throw new BusinessException(
                    "Table already has an open dining session"
            );
        }
    }
}