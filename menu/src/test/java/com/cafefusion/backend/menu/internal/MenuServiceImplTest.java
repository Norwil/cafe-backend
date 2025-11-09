package com.cafefusion.backend.menu.internal;

import com.cafefusion.backend.menu.api.model.CreateMenuItemRequest;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.menu.api.model.UpdateMenuItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void createMenuItem_shouldSaveAndReturnDto() {
        // Arrange
        CreateMenuItemRequest request = new CreateMenuItemRequest("New Coffee", "From Brazil", new BigDecimal("15.00"));

        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> {
            MenuItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        // Act
        MenuItemDto result = menuService.createMenuItem(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("New Coffee", result.name());

        ArgumentCaptor<MenuItem> itemCaptor = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemRepository).save(itemCaptor.capture());

        assertEquals("New Coffee", itemCaptor.getValue().getName());
        assertEquals(1L, itemCaptor.getValue().getId());
    }

    @Test
    void updateMenuItem_shouldUpdateAndReturnDto() {
        // Arrange
        UpdateMenuItemRequest request = new UpdateMenuItemRequest("Updated Name", "Updated Desc", BigDecimal.TEN);

        MenuItem existingItem = new MenuItem();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setPrice(BigDecimal.ONE);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MenuItemDto result = menuService.updateMenuItem(1L, request);

        // Assert & Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated Name", result.name());
        assertEquals(BigDecimal.TEN, result.price());

        verify(menuItemRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void deleteMenuItem_shouldThrowException_whenItemNotFound() {
        // Arrange
        when(menuItemRepository.existsById(99L)).thenReturn(false);

        // Act & Verify
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuService.deleteMenuItem(99L);;
        });

        assertEquals("Menu item not found: 99", exception.getMessage());

        verify(menuItemRepository, times(1)).existsById(99L);
        verify(menuItemRepository, never()).deleteById(anyLong());
    }
}
