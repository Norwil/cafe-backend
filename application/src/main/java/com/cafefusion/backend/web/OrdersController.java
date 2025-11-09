package com.cafefusion.backend.web;

import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersApi ordersApi;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createNewOrder(@RequestBody CreateOrderRequest request) {
        return ordersApi.createOrder(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long orderId) {
        return ordersApi.getOrderById(orderId)
                .map(orderDto -> ResponseEntity.ok(orderDto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public OrderDto updateOrderStatus(@PathVariable("id") Long orderId, @RequestBody UpdateOrderRequest request) {
        return ordersApi.updateOrderStatus(orderId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long orderId) {
        ordersApi.deleteOrder(orderId);
    }
}
