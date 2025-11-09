package com.cafefusion.backend.web;


import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.CreateMenuItemRequest;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.menu.api.model.UpdateMenuItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuApi menuApi;

    // --- PUBLIC ENDPOINTS ---

    @GetMapping
    public List<MenuItemDto> getAllMenuItems() {
        return menuApi.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        return menuApi.getMenuItemById(id)
                .map(item -> ResponseEntity.ok(item))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ADMIN ENDPOINTS ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItemDto createMenuItem(@RequestBody CreateMenuItemRequest request) {
        return menuApi.createMenuItem(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItemDto updateMenuItem(@PathVariable Long id, @RequestBody UpdateMenuItemRequest request) {
        return menuApi.updateMenuItem(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id) {
        menuApi.deleteMenuItem(id);
    }


}
