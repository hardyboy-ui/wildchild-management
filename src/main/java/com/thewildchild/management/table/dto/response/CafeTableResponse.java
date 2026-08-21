package com.thewildchild.management.table.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CafeTableResponse {

    private UUID id;
    private String tableNumber;
    private Integer capacity;
    private Integer displayOrder;
}