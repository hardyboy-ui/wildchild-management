package com.thewildchild.management.menu.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.dto.response.AddOnResponse;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.entity.MenuItemAddOn;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.menu.service.mapper.AddOnMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class MenuItemAddOnServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private AddOnRepository addOnRepository;

    @Mock
    private MenuItemAddOnRepository menuItemAddOnRepository;

    @Mock
    private AddOnMapper addOnMapper;

    @InjectMocks
    private MenuItemAddOnServiceImpl menuItemAddOnService;


    // =========================================================
    // ADD ADD-ON TO MENU ITEM
    // =========================================================

    @Test
    void shouldAddAddOnToMenuItemSuccessfully() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(true);

        AddOn addOn = new AddOn();
        addOn.setActive(true);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(menuItemAddOnRepository
                .existsByMenuItemIdAndAddOnId(menuItemId, addOnId))
                .thenReturn(false);

        // Act
        menuItemAddOnService.addAddOnToMenuItem(
                menuItemId,
                addOnId
        );

        // Verify
        ArgumentCaptor<MenuItemAddOn> captor =
                ArgumentCaptor.forClass(MenuItemAddOn.class);

        verify(menuItemAddOnRepository)
                .save(captor.capture());

        MenuItemAddOn mapping =
                captor.getValue();

        // Assert
        assertThat(mapping.getMenuItem())
                .isSameAs(menuItem);

        assertThat(mapping.getAddOn())
                .isSameAs(addOn);

        verify(menuItemRepository)
                .findById(menuItemId);

        verify(addOnRepository)
                .findById(addOnId);

        verify(menuItemAddOnRepository)
                .existsByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                );
    }


    @Test
    void shouldThrowExceptionWhenMenuItemNotFound() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + menuItemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(addOnRepository, never())
                .findById(any(UUID.class));

        verify(menuItemAddOnRepository, never())
                .existsByMenuItemIdAndAddOnId(
                        any(UUID.class),
                        any(UUID.class)
                );

        verify(menuItemAddOnRepository, never())
                .save(any(MenuItemAddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenMenuItemIsInactive() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(false);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + menuItemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(addOnRepository, never())
                .findById(any(UUID.class));

        verify(menuItemAddOnRepository, never())
                .save(any(MenuItemAddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenAddOnNotFound() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(true);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(addOnRepository)
                .findById(addOnId);

        verify(menuItemAddOnRepository, never())
                .existsByMenuItemIdAndAddOnId(
                        any(UUID.class),
                        any(UUID.class)
                );

        verify(menuItemAddOnRepository, never())
                .save(any(MenuItemAddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenAddOnIsInactive() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(true);

        AddOn addOn = new AddOn();
        addOn.setActive(false);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on not found with id: " + addOnId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(addOnRepository)
                .findById(addOnId);

        verify(menuItemAddOnRepository, never())
                .existsByMenuItemIdAndAddOnId(
                        any(UUID.class),
                        any(UUID.class)
                );

        verify(menuItemAddOnRepository, never())
                .save(any(MenuItemAddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenAddOnAlreadyAttached() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(true);

        AddOn addOn = new AddOn();
        addOn.setActive(true);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(addOnRepository.findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(menuItemAddOnRepository
                .existsByMenuItemIdAndAddOnId(menuItemId, addOnId))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.addAddOnToMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Add-on is already attached to this menu item"
                );

        // Verify
        verify(menuItemAddOnRepository)
                .existsByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                );

        verify(menuItemAddOnRepository, never())
                .save(any(MenuItemAddOn.class));
    }


    // =========================================================
    // REMOVE ADD-ON
    // =========================================================

    @Test
    void shouldRemoveAddOnFromMenuItemSuccessfully() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        MenuItemAddOn mapping =
                new MenuItemAddOn();

        when(menuItemAddOnRepository
                .findByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                ))
                .thenReturn(Optional.of(mapping));

        // Act
        menuItemAddOnService.removeAddOnFromMenuItem(
                menuItemId,
                addOnId
        );

        // Verify
        verify(menuItemAddOnRepository)
                .findByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                );

        verify(menuItemAddOnRepository)
                .delete(mapping);
    }


    @Test
    void shouldThrowExceptionWhenAddOnMappingNotFound() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();
        UUID addOnId = UUID.randomUUID();

        when(menuItemAddOnRepository
                .findByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                ))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.removeAddOnFromMenuItem(
                        menuItemId,
                        addOnId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Add-on is not attached to this menu item"
                );

        // Verify
        verify(menuItemAddOnRepository)
                .findByMenuItemIdAndAddOnId(
                        menuItemId,
                        addOnId
                );

        verify(menuItemAddOnRepository, never())
                .delete(any(MenuItemAddOn.class));
    }


    // =========================================================
    // GET ADD-ONS
    // =========================================================

    @Test
    void shouldGetAddOnsForMenuItemSuccessfully() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(true);

        AddOn addOn1 = new AddOn();
        AddOn addOn2 = new AddOn();

        MenuItemAddOn mapping1 =
                new MenuItemAddOn();

        MenuItemAddOn mapping2 =
                new MenuItemAddOn();

        mapping1.setMenuItem(menuItem);
        mapping1.setAddOn(addOn1);

        mapping2.setMenuItem(menuItem);
        mapping2.setAddOn(addOn2);

        AddOnResponse response1 =
                new AddOnResponse();

        AddOnResponse response2 =
                new AddOnResponse();

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        when(menuItemAddOnRepository
                .findAllByMenuItemIdAndAddOnActiveTrue(menuItemId))
                .thenReturn(List.of(mapping1, mapping2));

        when(addOnMapper.toResponse(addOn1))
                .thenReturn(response1);

        when(addOnMapper.toResponse(addOn2))
                .thenReturn(response2);

        // Act
        List<AddOnResponse> result =
                menuItemAddOnService.getAddOnsForMenuItem(
                        menuItemId
                );

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(menuItemAddOnRepository)
                .findAllByMenuItemIdAndAddOnActiveTrue(
                        menuItemId
                );

        verify(addOnMapper)
                .toResponse(addOn1);

        verify(addOnMapper)
                .toResponse(addOn2);
    }


    @Test
    void shouldThrowExceptionWhenGettingAddOnsForNonExistingMenuItem() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.getAddOnsForMenuItem(
                        menuItemId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + menuItemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(menuItemAddOnRepository, never())
                .findAllByMenuItemIdAndAddOnActiveTrue(
                        any(UUID.class)
                );

        verify(addOnMapper, never())
                .toResponse(any(AddOn.class));
    }


    @Test
    void shouldThrowExceptionWhenGettingAddOnsForInactiveMenuItem() {

        // Arrange
        UUID menuItemId = UUID.randomUUID();

        MenuItem menuItem = new MenuItem();
        menuItem.setActive(false);

        when(menuItemRepository.findById(menuItemId))
                .thenReturn(Optional.of(menuItem));

        // Act & Assert
        assertThatThrownBy(() ->
                menuItemAddOnService.getAddOnsForMenuItem(
                        menuItemId
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Menu item not found with id: " + menuItemId
                );

        // Verify
        verify(menuItemRepository)
                .findById(menuItemId);

        verify(menuItemAddOnRepository, never())
                .findAllByMenuItemIdAndAddOnActiveTrue(
                        any(UUID.class)
                );

        verify(addOnMapper, never())
                .toResponse(any(AddOn.class));
    }
}