package com.thewildchild.management.report.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface MenuItemSalesProjection {

    UUID getMenuItemId();

    String getMenuItemName();

    Long getQuantitySold();

    BigDecimal getRevenue();
}