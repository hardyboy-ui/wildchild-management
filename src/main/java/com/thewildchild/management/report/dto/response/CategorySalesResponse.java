package com.thewildchild.management.report.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CategorySalesResponse {

    private UUID categoryId;

    private String categoryName;

    private Long quantitySold;

    private BigDecimal revenue;
}