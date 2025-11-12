package com.cafefusion.backend.menu.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(
        @NotBlank(message = "Menu item name is required")
        @Size(max = 100, message = "Menu item name must be 100 characters or less")
        String name,

        @Size(max = 255, message = "Description must be 255 characters or less")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be a positive value")
        BigDecimal price
) {
}