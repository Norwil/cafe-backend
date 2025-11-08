package com.cafefusion.backend.menu.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
