package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.service.AddOnService;
import com.thewildchild.management.menu.service.mapper.AddOnMapper;
import com.thewildchild.management.menu.service.validator.AddOnValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AddOnServiceImpl implements AddOnService {

    private final AddOnRepository addOnRepository;
    private final AddOnMapper addOnMapper;
    private final AddOnValidator addOnValidator;

    @Override
    public AddOnResponse createAddOn(CreateAddOnRequest request) {

        addOnValidator.validateCreate(request);

        AddOn addOn = addOnMapper.toEntity(request);

        AddOn savedAddOn = addOnRepository.save(addOn);

        return addOnMapper.toResponse(savedAddOn);
    }

    @Override
    @Transactional(readOnly = true)
    public AddOnResponse getAddOnById(UUID addOnId) {

        AddOn addOn = findActiveAddOn(addOnId);

        return addOnMapper.toResponse(addOn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddOnResponse> getAllAddOns() {

        return addOnRepository.findAllByActiveTrue()
                .stream()
                .map(addOnMapper::toResponse)
                .toList();
    }

    @Override
    public AddOnResponse updateAddOn(
            UUID addOnId,
            UpdateAddOnRequest request
    ) {

        AddOn addOn = findActiveAddOn(addOnId);

        addOnValidator.validateUpdate(addOn, request);

        addOnMapper.updateEntity(addOn, request);

        return addOnMapper.toResponse(addOn);
    }

    @Override
    public void deleteAddOn(UUID addOnId) {

        AddOn addOn = findActiveAddOn(addOnId);

        addOn.setActive(false);
    }

    private AddOn findActiveAddOn(UUID addOnId) {

        return addOnRepository.findById(addOnId)
                .filter(AddOn::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Add-on not found with id: " + addOnId
                        )
                );
    }
}