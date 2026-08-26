package com.thewildchild.management.report.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface CategorySalesProjection {

    UUID getCategoryId();

    String getCategoryName();

    Long getQuantitySold();

    BigDecimal getRevenue();
}