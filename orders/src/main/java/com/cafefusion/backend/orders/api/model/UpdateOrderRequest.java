package com.cafefusion.backend.orders.api.model;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequest(
        @NotNull(message = "A new order status is required")
        OrderStatus newStatus
) {
}