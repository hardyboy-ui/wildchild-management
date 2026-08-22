package com.thewildchild.management.diningsession.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDiningSessionRequest {

    @NotNull(message = "Table ID is required")
    private UUID tableId;
}