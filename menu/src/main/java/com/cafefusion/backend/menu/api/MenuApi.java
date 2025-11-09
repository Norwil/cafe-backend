package com.cafefusion.backend.menu.api;

import com.cafefusion.backend.menu.api.model.CreateMenuItemRequest;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.menu.api.model.UpdateMenuItemRequest;

import java.util.List;
import java.util.Optional;


/**
 * This is the PUBLIC API for the Menu Module.
 * Other modules (line 'orders') will be use this interface to get information
 * about menu items, without knowing how this module works internally.
 */
public interface MenuApi {

    /**
     * finds a menu item by its unique ID.
     *
     * @param id The ID of the menu item.
     * @return An Optional containing the item's details if found, or empty if not.
     */
    Optional<MenuItemDto> getMenuItemById(Long id);

    /**
     * @return A lsit of all currently available menu items.
     */
    List<MenuItemDto> getAllMenuItems();

    /**
     * Creates a new menu item. (Admin Only)
     */
    MenuItemDto createMenuItem(CreateMenuItemRequest request);

    /**
     * Updates an existing menu item. (Admin Only)
     */
    MenuItemDto updateMenuItem(Long id, UpdateMenuItemRequest request);

    /**
     * Deletes a menu item. (Admin Only)
     */
    void deleteMenuItem(Long id);
}
