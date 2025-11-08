package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
        log.info("Total price calculated: {}", totalPrice);

        return new OrderDto(
                savedOrder.getId(),
                savedOrder.getCreatedAt(),
                savedOrder.getTotalPrice(),
                savedOrder.getItems().stream()
                        .map(OrderItem::getName)
                        .collect(Collectors.toList())
        );
    }
}
