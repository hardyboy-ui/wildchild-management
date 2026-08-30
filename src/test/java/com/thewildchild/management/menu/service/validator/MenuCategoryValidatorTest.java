package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryValidatorTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @InjectMocks
    private MenuCategoryValidator menuCategoryValidator;


    @Test
    void shouldAllowCreateWhenNameDoesNotExist() {

        // Arrange
        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("Burgers");

        when(menuCategoryRepository.existsByNameIgnoreCase("Burgers"))
                .thenReturn(false);

        // Act
        menuCategoryValidator.validateCreate(request);

        // Verify
        verify(menuCategoryRepository)
                .existsByNameIgnoreCase("Burgers");
    }


    @Test
    void shouldThrowExceptionWhenCreateNameAlreadyExists() {

        // Arrange
        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        request.setName("Burgers");

        when(menuCategoryRepository.existsByNameIgnoreCase("Burgers"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuCategoryValidator.validateCreate(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Menu category with name 'Burgers' already exists"
                );

        // Verify
        verify(menuCategoryRepository)
                .existsByNameIgnoreCase("Burgers");
    }


    @Test
    void shouldAllowUpdateWhenNameIsNull() {

        // Arrange
        MenuCategory category = new MenuCategory();
        category.setName("Burgers");

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName(null);

        // Act
        menuCategoryValidator.validateUpdate(
                category,
                request
        );

        // Verify
        verify(menuCategoryRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNameIsBlank() {

        // Arrange
        MenuCategory category = new MenuCategory();
        category.setName("Burgers");

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("   ");

        // Act
        menuCategoryValidator.validateUpdate(
                category,
                request
        );

        // Verify
        verify(menuCategoryRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNameIsUnchanged() {

        // Arrange
        MenuCategory category = new MenuCategory();
        category.setName("Burgers");

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("burgers");

        // Act
        menuCategoryValidator.validateUpdate(
                category,
                request
        );

        // Verify
        verify(menuCategoryRepository, never())
                .existsByNameIgnoreCase(anyString());
    }


    @Test
    void shouldAllowUpdateWhenNewNameDoesNotExist() {

        // Arrange
        MenuCategory category = new MenuCategory();
        category.setName("Burgers");

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("Pizza");

        when(menuCategoryRepository.existsByNameIgnoreCase("Pizza"))
                .thenReturn(false);

        // Act
        menuCategoryValidator.validateUpdate(
                category,
                request
        );

        // Verify
        verify(menuCategoryRepository)
                .existsByNameIgnoreCase("Pizza");
    }


    @Test
    void shouldThrowExceptionWhenUpdateNameAlreadyExists() {

        // Arrange
        MenuCategory category = new MenuCategory();
        category.setName("Burgers");

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        request.setName("Pizza");

        when(menuCategoryRepository.existsByNameIgnoreCase("Pizza"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuCategoryValidator.validateUpdate(
                        category,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Menu category with name 'Pizza' already exists"
                );

        // Verify
        verify(menuCategoryRepository)
                .existsByNameIgnoreCase("Pizza");
    }
}