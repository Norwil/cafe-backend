package com.cafefusion.backend.menu.api;

import com.cafefusion.backend.menu.api.model.MenuItemDto;

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
}
