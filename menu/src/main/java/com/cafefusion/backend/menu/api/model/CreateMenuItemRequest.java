package com.cafefusion.backend.menu.api.model;

import java.math.BigDecimal;

public record CreateMenuItemRequest (
    String name,
    String description,
    BigDecimal price
) {
}
