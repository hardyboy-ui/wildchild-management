package com.thewildchild.management.menu.service.validator;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemValidatorTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemValidator menuItemValidator;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldAllowCreateWhenNameDoesNotExistInCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setName("Chicken Burger");
        request.setCategoryId(categoryId);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        categoryId
                ))
                .thenReturn(false);

        // Act
        menuItemValidator.validateCreate(request);

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        categoryId
                );
    }


    @Test
    void shouldThrowExceptionWhenCreateNameAlreadyExistsInCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setName("Chicken Burger");
        request.setCategoryId(categoryId);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        categoryId
                ))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemValidator.validateCreate(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Menu item with name 'Chicken Burger' "
                                + "already exists in this category"
                );

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        categoryId
                );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldAllowUpdateWhenNothingChanges() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategory category =
                new MenuCategory();

        category.setId(categoryId);

        MenuItem item =
                new MenuItem();

        item.setName("Chicken Burger");
        item.setCategory(category);

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName(null);
        request.setCategoryId(null);

        // Act
        menuItemValidator.validateUpdate(
                item,
                request
        );

        // Verify
        verify(menuItemRepository, never())
                .existsByNameIgnoreCaseAndCategoryId(
                        anyString(),
                        any(UUID.class)
                );
    }


    @Test
    void shouldAllowUpdateWhenOnlyNameChangesAndNewNameDoesNotExist() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategory category =
                new MenuCategory();

        category.setId(categoryId);

        MenuItem item =
                new MenuItem();

        item.setName("Chicken Burger");
        item.setCategory(category);

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName("Veg Burger");
        request.setCategoryId(null);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Veg Burger",
                        categoryId
                ))
                .thenReturn(false);

        // Act
        menuItemValidator.validateUpdate(
                item,
                request
        );

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Veg Burger",
                        categoryId
                );
    }


    @Test
    void shouldThrowExceptionWhenNewNameAlreadyExistsInSameCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategory category =
                new MenuCategory();

        category.setId(categoryId);

        MenuItem item =
                new MenuItem();

        item.setName("Chicken Burger");
        item.setCategory(category);

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName("Veg Burger");
        request.setCategoryId(null);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Veg Burger",
                        categoryId
                ))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemValidator.validateUpdate(
                        item,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Menu item with name 'Veg Burger' "
                                + "already exists in this category"
                );

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Veg Burger",
                        categoryId
                );
    }


    @Test
    void shouldAllowUpdateWhenOnlyCategoryChangesAndNameDoesNotExist() {

        // Arrange
        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        MenuCategory category =
                new MenuCategory();

        category.setId(oldCategoryId);

        MenuItem item =
                new MenuItem();

        item.setName("Chicken Burger");
        item.setCategory(category);

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName(null);
        request.setCategoryId(newCategoryId);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        newCategoryId
                ))
                .thenReturn(false);

        // Act
        menuItemValidator.validateUpdate(
                item,
                request
        );

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        newCategoryId
                );
    }


    @Test
    void shouldThrowExceptionWhenItemAlreadyExistsInNewCategory() {

        // Arrange
        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        MenuCategory category =
                new MenuCategory();

        category.setId(oldCategoryId);

        MenuItem item =
                new MenuItem();

        item.setName("Chicken Burger");
        item.setCategory(category);

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setName(null);
        request.setCategoryId(newCategoryId);

        when(menuItemRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        newCategoryId
                ))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemValidator.validateUpdate(
                        item,
                        request
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Menu item with name 'Chicken Burger' "
                                + "already exists in this category"
                );

        // Verify
        verify(menuItemRepository)
                .existsByNameIgnoreCaseAndCategoryId(
                        "Chicken Burger",
                        newCategoryId
                );
    }
}