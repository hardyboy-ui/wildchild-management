package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateMenuItemRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuItemRequest;
import com.thewildchild.management.menu.dto.response.MenuItemResponse;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.menu.service.mapper.MenuItemMapper;
import com.thewildchild.management.menu.service.validator.MenuItemValidator;
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
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @Mock
    private MenuItemValidator menuItemValidator;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateMenuItemSuccessfully() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setCategoryId(categoryId);

        MenuCategory category = new MenuCategory();
        category.setActive(true);

        MenuItem item = new MenuItem();
        MenuItem savedItem = new MenuItem();

        MenuItemResponse response =
                new MenuItemResponse();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(menuItemMapper.toEntity(request))
                .thenReturn(item);

        when(menuItemRepository.save(item))
                .thenReturn(savedItem);

        when(menuItemMapper.toResponse(savedItem))
                .thenReturn(response);

        // Act
        MenuItemResponse result =
                menuItemService.createMenuItem(request);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuItemValidator)
                .validateCreate(request);

        verify(menuItemMapper)
                .toEntity(request);

        verify(menuItemRepository)
                .save(item);

        verify(menuItemMapper)
                .toResponse(savedItem);
    }


    @Test
    void shouldThrowExceptionWhenCreatingWithNonExistingCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setCategoryId(categoryId);

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.createMenuItem(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Active menu category not found with id: "
                                + categoryId
                );

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuItemValidator, never())
                .validateCreate(request);

        verify(menuItemMapper, never())
                .toEntity(request);

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    @Test
    void shouldThrowExceptionWhenCreatingWithInactiveCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest();

        request.setCategoryId(categoryId);

        MenuCategory category = new MenuCategory();
        category.setActive(false);

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.createMenuItem(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Active menu category not found with id: "
                                + categoryId
                );

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuItemValidator, never())
                .validateCreate(request);

        verify(menuItemRepository, never())
                .save(any(MenuItem.class));
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetMenuItemByIdSuccessfully() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        MenuItem item = new MenuItem();
        item.setActive(true);

        MenuItemResponse response =
                new MenuItemResponse();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        // Act
        MenuItemResponse result =
                menuItemService.getMenuItemById(itemId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemMapper)
                .toResponse(item);
    }


    @Test
    void shouldThrowExceptionWhenMenuItemNotFound() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.getMenuItemById(itemId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + itemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemMapper, never())
                .toResponse(any(MenuItem.class));
    }


    @Test
    void shouldThrowExceptionWhenGettingInactiveMenuItem() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        MenuItem item = new MenuItem();
        item.setActive(false);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.getMenuItemById(itemId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + itemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemMapper, never())
                .toResponse(any(MenuItem.class));
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllActiveMenuItemsSuccessfully() {

        // Arrange
        MenuItem item1 = new MenuItem();
        MenuItem item2 = new MenuItem();

        MenuItemResponse response1 =
                new MenuItemResponse();

        MenuItemResponse response2 =
                new MenuItemResponse();

        when(menuItemRepository.findAllByActiveTrue())
                .thenReturn(List.of(item1, item2));

        when(menuItemMapper.toResponse(item1))
                .thenReturn(response1);

        when(menuItemMapper.toResponse(item2))
                .thenReturn(response2);

        // Act
        List<MenuItemResponse> result =
                menuItemService.getAllMenuItems();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        // Verify
        verify(menuItemRepository)
                .findAllByActiveTrue();

        verify(menuItemMapper)
                .toResponse(item1);

        verify(menuItemMapper)
                .toResponse(item2);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateMenuItemSuccessfullyWithoutChangingCategory() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setCategoryId(null);

        MenuItem item = new MenuItem();
        item.setActive(true);

        MenuItemResponse response =
                new MenuItemResponse();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        // Act
        MenuItemResponse result =
                menuItemService.updateMenuItem(
                        itemId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemValidator)
                .validateUpdate(item, request);

        verify(menuItemMapper)
                .updateEntity(item, request);

        verify(menuCategoryRepository, never())
                .findById(any(UUID.class));

        verify(menuItemMapper)
                .toResponse(item);
    }


    @Test
    void shouldUpdateMenuItemSuccessfullyWithNewCategory() {

        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setCategoryId(categoryId);

        MenuItem item = new MenuItem();
        item.setActive(true);

        MenuCategory category = new MenuCategory();
        category.setActive(true);

        MenuItemResponse response =
                new MenuItemResponse();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(menuItemMapper.toResponse(item))
                .thenReturn(response);

        // Act
        MenuItemResponse result =
                menuItemService.updateMenuItem(
                        itemId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        assertThat(item.getCategory())
                .isSameAs(category);

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemValidator)
                .validateUpdate(item, request);

        verify(menuItemMapper)
                .updateEntity(item, request);

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuItemMapper)
                .toResponse(item);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingMenuItem() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.updateMenuItem(
                        itemId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + itemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemValidator, never())
                .validateUpdate(
                        any(MenuItem.class),
                        any(UpdateMenuItemRequest.class)
                );

        verify(menuItemMapper, never())
                .updateEntity(
                        any(MenuItem.class),
                        any(UpdateMenuItemRequest.class)
                );

        verify(menuItemMapper, never())
                .toResponse(any(MenuItem.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingWithInactiveCategory() {

        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        UpdateMenuItemRequest request =
                new UpdateMenuItemRequest();

        request.setCategoryId(categoryId);

        MenuItem item = new MenuItem();
        item.setActive(true);

        MenuCategory category = new MenuCategory();
        category.setActive(false);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.updateMenuItem(
                        itemId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Active menu category not found with id: "
                                + categoryId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemValidator)
                .validateUpdate(item, request);

        verify(menuItemMapper)
                .updateEntity(item, request);

        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuItemMapper, never())
                .toResponse(any(MenuItem.class));
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteMenuItemSuccessfully() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        MenuItem item = new MenuItem();
        item.setActive(true);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        // Act
        menuItemService.deleteMenuItem(itemId);

        // Assert
        assertThat(item.isActive())
                .isFalse();

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemRepository, never())
                .delete(any(MenuItem.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingMenuItem() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.deleteMenuItem(itemId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + itemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemRepository, never())
                .delete(any(MenuItem.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingInactiveMenuItem() {

        // Arrange
        UUID itemId = UUID.randomUUID();

        MenuItem item = new MenuItem();
        item.setActive(false);

        when(menuItemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemService.deleteMenuItem(itemId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + itemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(itemId);

        verify(menuItemRepository, never())
                .delete(any(MenuItem.class));
    }
}