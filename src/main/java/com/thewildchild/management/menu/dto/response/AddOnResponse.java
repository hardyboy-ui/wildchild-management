package com.thewildchild.management.menu.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class AddOnResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
}