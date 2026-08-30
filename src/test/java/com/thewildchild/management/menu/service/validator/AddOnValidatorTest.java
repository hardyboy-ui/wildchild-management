package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateAddOnRequest;
import com.thewildchild.management.menu.dto.request.UpdateAddOnRequest;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOnValidatorTest {

    @Mock
    private AddOnRepository addOnRepository;

    @InjectMocks
    private AddOnValidator addOnValidator;


    @Test
    void shouldAllowCreateWhenNameDoesNotExist() {

        // Arrange
        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("Extra Cheese");

        when(addOnRepository.existsByNameIgnoreCase("Extra Cheese"))
                .thenReturn(false);

        // Act
        addOnValidator.validateCreate(request);

        // Verify
        verify(addOnRepository)
                .existsByNameIgnoreCase("Extra Cheese");
    }


    @Test
    void shouldThrowExceptionWhenCreateNameAlreadyExists() {

        // Arrange
        CreateAddOnRequest request =
                new CreateAddOnRequest();

        request.setName("Extra Cheese");

        when(addOnRepository.existsByNameIgnoreCase("Extra Cheese"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                addOnValidator.validateCreate(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Add-on with name 'Extra Cheese' already exists"
                );

        // Verify
        verify(addOnRepository)
                .existsByNameIgnoreCase("Extra Cheese");
    }


    @Test
    void shouldAllowUpdateWhenNameIsNull() {

        // Arrange
        AddOn addOn = new AddOn();
        addOn.setName("Extra Cheese");

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName(null);

        // Act
        addOnValidator.validateUpdate(addOn, request);

        // Verify
        verify(addOnRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNameIsBlank() {

        // Arrange
        AddOn addOn = new AddOn();
        addOn.setName("Extra Cheese");

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("   ");

        // Act
        addOnValidator.validateUpdate(addOn, request);

        // Verify
        verify(addOnRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNameIsUnchanged() {

        // Arrange
        AddOn addOn = new AddOn();
        addOn.setName("Extra Cheese");

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("extra cheese");

        // Act
        addOnValidator.validateUpdate(addOn, request);

        // Verify
        verify(addOnRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNewNameDoesNotExist() {

        // Arrange
        AddOn addOn = new AddOn();
        addOn.setName("Extra Cheese");

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("Extra Sauce");

        when(addOnRepository.existsByNameIgnoreCase("Extra Sauce"))
                .thenReturn(false);

        // Act
        addOnValidator.validateUpdate(addOn, request);

        // Verify
        verify(addOnRepository)
                .existsByNameIgnoreCase("Extra Sauce");
    }


    @Test
    void shouldThrowExceptionWhenUpdateNameAlreadyExists() {

        // Arrange
        AddOn addOn = new AddOn();
        addOn.setName("Extra Cheese");

        UpdateAddOnRequest request =
                new UpdateAddOnRequest();

        request.setName("Extra Sauce");

        when(addOnRepository.existsByNameIgnoreCase("Extra Sauce"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                addOnValidator.validateUpdate(addOn, request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Add-on with name 'Extra Sauce' already exists"
                );

        // Verify
        verify(addOnRepository)
                .existsByNameIgnoreCase("Extra Sauce");
    }
}