package com.cafefusion.backend.orders.api.model;

import java.util.List;

public record CreateOrderRequest(
        // A list of the IDs of the menu items the customer wants to buy
        List<Long> menuItemIds
) {
}
