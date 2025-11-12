package com.cafefusion.backend.orders.api.model;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        // A list of the IDs of the menu items the customer wants to buy
        @NotEmpty(message = "Order must contain at least one menu item")
        List<Long> menuItemIds
) {
}
