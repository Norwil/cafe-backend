package com.cafefusion.backend.orders.api.model;

public record UpdateOrderRequest(
        OrderStatus newStatus
) {
}