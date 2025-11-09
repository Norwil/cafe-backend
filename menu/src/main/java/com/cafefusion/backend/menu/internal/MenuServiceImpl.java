package com.cafefusion.backend.menu.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.CreateMenuItemRequest;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.menu.api.model.UpdateMenuItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuApi {

    private final MenuItemRepository menuItemRepository;

    @Override
    public Optional<MenuItemDto> getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    public List<MenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuItemDto createMenuItem(CreateMenuItemRequest request) {
        MenuItem newItem = new MenuItem();
        newItem.setName(request.name());
        newItem.setDescription(request.description());
        newItem.setPrice(request.price());

        MenuItem savedItem = menuItemRepository.save(newItem);
        return toDto(savedItem);
    }

    @Override
    @Transactional
    public MenuItemDto updateMenuItem(Long id, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));

        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());

        MenuItem updatedItem = menuItemRepository.save(item);
        return toDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    /**
     * A private helper method to convert our internal 'MenuItem' entity
     * into a public 'MenuItemDto' for the API.
     */
    private MenuItemDto toDto(MenuItem entity) {
        return new MenuItemDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }
}
