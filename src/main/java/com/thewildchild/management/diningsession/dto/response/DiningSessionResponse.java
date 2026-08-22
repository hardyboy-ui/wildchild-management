package com.thewildchild.management.diningsession.dto.response;

import com.thewildchild.management.diningsession.entity.DiningSessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DiningSessionResponse {

    private UUID id;
    private String sessionNumber;

    private UUID tableId;
    private String tableNumber;

    private DiningSessionStatus status;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}