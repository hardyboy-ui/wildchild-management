package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.request.CreateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.request.UpdateMenuCategoryRequest;
import com.thewildchild.management.menu.dto.response.MenuCategoryResponse;
import com.thewildchild.management.menu.entity.MenuCategory;
import com.thewildchild.management.menu.repository.MenuCategoryRepository;
import com.thewildchild.management.menu.service.mapper.MenuCategoryMapper;
import com.thewildchild.management.menu.service.validator.MenuCategoryValidator;
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
class MenuCategoryServiceImplTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuCategoryMapper menuCategoryMapper;

    @Mock
    private MenuCategoryValidator menuCategoryValidator;

    @InjectMocks
    private MenuCategoryServiceImpl menuCategoryService;


    // CREATE

    @Test
    void shouldCreateCategorySuccessfully() {

        // Arrange
        CreateMenuCategoryRequest request =
                new CreateMenuCategoryRequest();

        MenuCategory category = new MenuCategory();
        MenuCategory savedCategory = new MenuCategory();

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        when(menuCategoryMapper.toEntity(request))
                .thenReturn(category);

        when(menuCategoryRepository.save(category))
                .thenReturn(savedCategory);

        when(menuCategoryMapper.toResponse(savedCategory))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.createCategory(request);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuCategoryValidator)
                .validateCreate(request);

        verify(menuCategoryMapper)
                .toEntity(request);

        verify(menuCategoryRepository)
                .save(category);

        verify(menuCategoryMapper)
                .toResponse(savedCategory);
    }


    // GET BY ID

    @Test
    void shouldGetCategoryByIdSuccessfully() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategory category = new MenuCategory();

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(menuCategoryMapper.toResponse(category))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.getCategoryById(categoryId);

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryMapper)
                .toResponse(category);
    }


    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuCategoryService.getCategoryById(categoryId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu category not found with id: " + categoryId
                );

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryMapper, never())
                .toResponse(any(MenuCategory.class));
    }


    // GET ALL

    @Test
    void shouldGetAllActiveCategoriesSuccessfully() {

        // Arrange
        MenuCategory category1 = new MenuCategory();
        MenuCategory category2 = new MenuCategory();

        MenuCategoryResponse response1 =
                new MenuCategoryResponse();

        MenuCategoryResponse response2 =
                new MenuCategoryResponse();

        when(menuCategoryRepository.findAllByActiveTrue())
                .thenReturn(List.of(category1, category2));

        when(menuCategoryMapper.toResponse(category1))
                .thenReturn(response1);

        when(menuCategoryMapper.toResponse(category2))
                .thenReturn(response2);

        // Act
        List<MenuCategoryResponse> result =
                menuCategoryService.getAllCategories();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        // Verify
        verify(menuCategoryRepository)
                .findAllByActiveTrue();

        verify(menuCategoryMapper)
                .toResponse(category1);

        verify(menuCategoryMapper)
                .toResponse(category2);
    }


    // UPDATE

    @Test
    void shouldUpdateCategorySuccessfully() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        MenuCategory category = new MenuCategory();

        MenuCategoryResponse response =
                new MenuCategoryResponse();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(menuCategoryRepository.save(category))
                .thenReturn(category);

        when(menuCategoryMapper.toResponse(category))
                .thenReturn(response);

        // Act
        MenuCategoryResponse result =
                menuCategoryService.updateCategory(
                        categoryId,
                        request
                );

        // Assert
        assertThat(result)
                .isSameAs(response);

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryValidator)
                .validateUpdate(category, request);

        verify(menuCategoryMapper)
                .updateEntity(category, request);

        verify(menuCategoryRepository)
                .save(category);

        verify(menuCategoryMapper)
                .toResponse(category);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        UpdateMenuCategoryRequest request =
                new UpdateMenuCategoryRequest();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuCategoryService.updateCategory(
                        categoryId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu category not found with id: " + categoryId
                );

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryValidator, never())
                .validateUpdate(
                        any(MenuCategory.class),
                        any(UpdateMenuCategoryRequest.class)
                );

        verify(menuCategoryMapper, never())
                .updateEntity(
                        any(MenuCategory.class),
                        any(UpdateMenuCategoryRequest.class)
                );

        verify(menuCategoryRepository, never())
                .save(any(MenuCategory.class));
    }


    // DELETE

    @Test
    void shouldDeleteCategorySuccessfully() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        MenuCategory category = new MenuCategory();
        category.setActive(true);

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        // Act
        menuCategoryService.deleteCategory(categoryId);

        // Assert
        assertThat(category.isActive())
                .isFalse();

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryRepository, never())
                .delete(any(MenuCategory.class));
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCategory() {

        // Arrange
        UUID categoryId = UUID.randomUUID();

        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuCategoryService.deleteCategory(categoryId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu category not found with id: " + categoryId
                );

        // Verify
        verify(menuCategoryRepository)
                .findById(categoryId);

        verify(menuCategoryRepository, never())
                .delete(any(MenuCategory.class));
    }
}