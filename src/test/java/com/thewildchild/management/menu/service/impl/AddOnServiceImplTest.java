package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.service.mapper.AddOnMapper;
import com.thewildchild.management.menu.service.validator.AddOnValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOnServiceImplTest {

    @Mock
    private AddOnRepository addOnRepository;

    @Mock
    private AddOnMapper addOnMapper;

    @Mock
    private AddOnValidator addOnValidator;

    @InjectMocks
    private AddOnServiceImpl addOnService;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateAddOnSuccessfully() {

        // Arrange
        CreateAddOnRequest request =
                new CreateAddOnRequest();

        AddOn addOn = new AddOn();
        AddOn savedAddOn = new AddOn();

        AddOnResponse response =
                new AddOnResponse();

        when(addOnMapper.toEntity(request))
                .thenReturn(addOn);

        when(addOnRepository.save(addOn))
                .thenReturn(savedAddOn);

        when(addOnMapper.toResponse(savedAddOn))
                .thenReturn(response);

        // Act
        AddOnResponse result =
                addOnService.createAddOn(request);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(addOnValidator)
                .validateCreate(request);

        verify(addOnMapper)
                .toEntity(request);

        verify(addOnRepository)
                .save(addOn);

        verify(addOnMapper)
                .toResponse(savedAddOn);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetAddOnByIdSuccessfully() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOn addOn = new AddOn();
        addOn.setActive(true);

        AddOnResponse response =
                new AddOnResponse();

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(addOnMapper.toResponse(addOn))
                .thenReturn(response);

        // Act
        AddOnResponse result =
                addOnService.getAddOnById(addOnId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnMapper)
                .toResponse(addOn);
    }


    @Test
    void shouldThrowExceptionWhenAddOnNotFound() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.getAddOnById(addOnId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnMapper, never())
                .toResponse(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenGettingInactiveAddOn() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOn addOn = new AddOn();
        addOn.setActive(false);

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.getAddOnById(addOnId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnMapper, never())
                .toResponse(any(AddOn.class));
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllActiveAddOnsSuccessfully() {

        // Arrange
        AddOn addOn1 = new AddOn();
        AddOn addOn2 = new AddOn();

        AddOnResponse response1 =
                new AddOnResponse();

        AddOnResponse response2 =
                new AddOnResponse();

        when(addOnRepository.findAllByActiveTrue())
                .thenReturn(List.of(addOn1, addOn2));

        when(addOnMapper.toResponse(addOn1))
                .thenReturn(response1);

        when(addOnMapper.toResponse(addOn2))
                .thenReturn(response2);

        // Act
        List<AddOnResponse> result =
                addOnService.getAllAddOns();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        // Verify
        verify(addOnRepository)
                .findAllByActiveTrue();

        verify(addOnMapper)
                .toResponse(addOn1);

        verify(addOnMapper)
                .toResponse(addOn2);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateAddOnSuccessfully() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        AddOn addOn = new AddOn();
        addOn.setActive(true);

        AddOnResponse response =
                new AddOnResponse();

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(addOnMapper.toResponse(addOn))
                .thenReturn(response);

        // Act
        AddOnResponse result =
                addOnService.updateAddOn(
                        addOnId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnValidator)
                .validateUpdate(addOn, request);

        verify(addOnMapper)
                .updateEntity(addOn, request);

        verify(addOnMapper)
                .toResponse(addOn);

        verify(addOnRepository, never())
                .save(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAddOn() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.updateAddOn(
                        addOnId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnValidator, never())
                .validateUpdate(
                        any(AddOn.class),
                        any(UpdateAddOnRequest.class)
                );

        verify(addOnMapper, never())
                .updateEntity(
                        any(AddOn.class),
                        any(UpdateAddOnRequest.class)
                );

        verify(addOnMapper, never())
                .toResponse(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingInactiveAddOn() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        AddOn addOn = new AddOn();
        addOn.setActive(false);

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.updateAddOn(
                        addOnId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnValidator, never())
                .validateUpdate(
                        any(AddOn.class),
                        any(UpdateAddOnRequest.class)
                );

        verify(addOnMapper, never())
                .updateEntity(
                        any(AddOn.class),
                        any(UpdateAddOnRequest.class)
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteAddOnSuccessfully() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOn addOn = new AddOn();
        addOn.setActive(true);

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        // Act
        addOnService.deleteAddOn(addOnId);

        // Assert
        assertThat(addOn.isActive())
                .isFalse();

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnRepository, never())
                .delete(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAddOn() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.deleteAddOn(addOnId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnRepository, never())
                .delete(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingInactiveAddOn() {

        // Arrange
        UUID addOnId = UUID.randomUUID();

        AddOn addOn = new AddOn();
        addOn.setActive(false);

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        // Act & Assert
        assertThatThrownBy(() ->
                addOnService.deleteAddOn(addOnId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(addOnRepository)
                .findById(addOnId);

        verify(addOnRepository, never())
                .delete(any(AddOn.class));
    }
}