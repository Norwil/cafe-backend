package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;

import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j  // To add a logger, so we can see output
public class OrdersServiceImpl implements OrdersApi {

    private final MenuApi menuApi;

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        log.info("Creating new order from items: {}", request.menuItemIds());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        Order newOrder = new Order(Instant.now(), BigDecimal.ZERO);

        for (Long itemId : request.menuItemIds()) {
            MenuItemDto item = menuApi.getMenuItemById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            // Add its price to the total
            totalPrice = totalPrice.add(item.price());

            orderItems.add(new OrderItem(
                    newOrder,
                    item.id(),
                    item.name(),
                    item.price()
            ));
        }

        newOrder.setTotalPrice(totalPrice);
        newOrder.setItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);

        log.info("Successfully saved new order with ID: {}", savedOrder.getId());

       return toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long orderId) {
        log.info("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderRequest request) {
        log.info("Updating order {} to status {}", orderId, request.newStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(request.newStatus());

        return toDto(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("Deleting order by ID: {}", orderId);
        if(!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    /**
     * A private helper method to map our internal Order entity to our
     * public OrderDto. This keeps our code clean and avoids repetition.
     */
    private OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getItems().stream()
                        .map(OrderItem::getName)
                        .collect(Collectors.toList())
        );
    }
}
