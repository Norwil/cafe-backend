package com.cafefusion.backend.orders.api.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long orderId,
        Instant createdAt,
        OrderStatus status,
        BigDecimal totalPrice,
        List<String> itemNames
) {
}
