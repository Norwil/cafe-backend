package com.cafefusion.backend.menu.api.model;

import java.math.BigDecimal;

/**
 * We use a 'record' for a concise, immutable data carrier.
 */
public record MenuItemDto(
        Long id,
        String name,
        String description,
        BigDecimal price
) {
}
