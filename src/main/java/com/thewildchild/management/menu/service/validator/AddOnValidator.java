package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddOnValidator {

    private final AddOnRepository addOnRepository;

    public void validateCreate(CreateAddOnRequest request) {

        if (addOnRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException(
                    "Add-on with name '" + request.getName() + "' already exists"
            );
        }
    }

    public void validateUpdate(
            AddOn addOn,
            UpdateAddOnRequest request
    ) {

        if (request.getName() != null
                && !request.getName().isBlank()
                && !request.getName().equalsIgnoreCase(addOn.getName())
                && addOnRepository.existsByNameIgnoreCase(request.getName())) {

            throw new BusinessException(
                    "Add-on with name '" + request.getName() + "' already exists"
            );
        }
    }
}