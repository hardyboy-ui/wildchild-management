package com.thewildchild.management.menu.service.mapper;

import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.entity.AddOn;
import org.springframework.stereotype.Component;

@Component
public class AddOnMapper {

    public AddOn toEntity(CreateAddOnRequest request) {
        AddOn addOn = new AddOn();

        addOn.setName(request.getName());
        addOn.setDescription(request.getDescription());
        addOn.setPrice(request.getPrice());

        return addOn;
    }

    public void updateEntity(
            AddOn addOn,
            UpdateAddOnRequest request
    ) {
        if (request.getName() != null && !request.getName().isBlank()) {
            addOn.setName(request.getName());
        }

        if (request.getDescription() != null) {
            addOn.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            addOn.setPrice(request.getPrice());
        }
    }

    public AddOnResponse toResponse(AddOn addOn) {
        AddOnResponse response = new AddOnResponse();

        response.setId(addOn.getId());
        response.setName(addOn.getName());
        response.setDescription(addOn.getDescription());
        response.setPrice(addOn.getPrice());

        return response;
    }
}