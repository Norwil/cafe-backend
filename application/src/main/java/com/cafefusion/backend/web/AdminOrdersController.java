package com.cafefusion.backend.web;

import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrdersController {

    private final OrdersApi ordersApi;

    @GetMapping
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return  ordersApi.getOrders(pageable);
    }

    @GetMapping("/filter")
    public Page<OrderDto> getOrdersByStatus(
            @RequestParam List<OrderStatus> statuses,
            Pageable pageable
    ) {
        return ordersApi.getOrdersByStatuses(statuses, pageable);
    }

    @GetMapping("/stats")
    public Map<OrderStatus, Long> getStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        Instant startInstant = (start != null) ? Instant.parse(start) : null;
        Instant endInstant = (end != null) ? Instant.parse(end) : null;

        return ordersApi.getOrderStatistics(startInstant, endInstant);
    }

    @GetMapping("/kitchen")
    public Page<OrderDto> getKitchenOrders(Pageable pageable) {
        return ordersApi.getKitchenOrders(pageable);
    }

    @PutMapping("/{id}/status")
    public OrderDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        return ordersApi.updateStatusValidated(id, request);
    }


}
