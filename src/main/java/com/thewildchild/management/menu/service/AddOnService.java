package com.thewildchild.management.menu.service;

import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;

import java.util.List;
import java.util.UUID;

public interface AddOnService {

    AddOnResponse createAddOn(CreateAddOnRequest request);

    AddOnResponse getAddOnById(UUID addOnId);

    List<AddOnResponse> getAllAddOns();

    AddOnResponse updateAddOn(
            UUID addOnId,
            UpdateAddOnRequest request
    );

    void deleteAddOn(UUID addOnId);
}