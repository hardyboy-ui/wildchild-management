package com.thewildchild.management.report.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class MenuItemSalesResponse {

    private UUID menuItemId;

    private String menuItemName;

    private Long quantitySold;

    private BigDecimal revenue;
}