package com.thewildchild.management.menu.service;

import com.thewildchild.management.menu.dto.response.AddOnResponse;

import java.util.List;
import java.util.UUID;

public interface MenuItemAddOnService {

    void addAddOnToMenuItem(UUID menuItemId, UUID addOnId);

    void removeAddOnFromMenuItem(UUID menuItemId, UUID addOnId);

    List<AddOnResponse> getAddOnsForMenuItem(UUID menuItemId);
}