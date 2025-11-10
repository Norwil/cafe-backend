package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.exception.InvalidStatusTransitionException;
import com.cafefusion.backend.orders.api.exception.OrderNotFoundException;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.cafefusion.backend.users.internal.User;

import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.cafefusion.backend.orders.api.model.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j  // To add a logger, so we can see output
public class OrdersServiceImpl implements OrdersApi {

    private final MenuApi menuApi;

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();

        log.info("Creating new order for user ID {}: {}", currentUserId, request.menuItemIds());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        Order newOrder = new Order(currentUserId, Instant.now(), BigDecimal.ZERO);

        for (Long itemId : request.menuItemIds()) {
            MenuItemDto item = menuApi.getMenuItemById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
            totalPrice = totalPrice.add(item.price());
            orderItems.add(new OrderItem(newOrder, item.id(), item.name(), item.price()));
        }

        newOrder.setTotalPrice(totalPrice);
        newOrder.setItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);
        log.info("Successfully saved new order {} for user {}", savedOrder.getId(), currentUserId);

        return toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrderHistory() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = currentUser.getId();

        log.info("Fetching order history for user ID: {}", currentUserId);

        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatuses(List<OrderStatus> statuses, Pageable pageable) {
        return orderRepository.findByStatusIn(statuses, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<OrderStatus, Long> getOrderStatistics(Instant start, Instant end) {
        Map<OrderStatus, Long> stats = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            long count;
            if (start != null && end != null) {
                count = orderRepository.countByStatusAndCreatedAtBetween(status, start, end);
            } else {
                count = orderRepository.countByStatus(status);
            }
            stats.put(status, count);
        }
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getKitchenOrders(Pageable pageable) {
        List<OrderStatus> kitchenStatuses = List.of(
                OrderStatus.IN_PROGRESS,
                OrderStatus.READY_FOR_PICKUP
        );

        return orderRepository.findByStatusIn(kitchenStatuses, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public OrderDto updateStatusValidated(Long orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isValidTransition(order.getStatus(), request.newStatus())) {
            throw new InvalidStatusTransitionException(order.getStatus(), request.newStatus());
        }

        order.setStatus(request.newStatus());

        return toDto(order);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING_APPROVAL -> List.of(CONFIRMED, CANCELLED).contains(to);
            case CONFIRMED -> List.of(IN_PROGRESS, CANCELLED).contains(to);
            case IN_PROGRESS -> List.of(READY_FOR_PICKUP).contains(to);
            case READY_FOR_PICKUP -> List.of(COMPLETED).contains(to);
            case COMPLETED, CANCELLED -> false;
        };
    }

    /**
     * A private helper method to map our internal Order entity to our
     * public OrderDto. This keeps our code clean and avoids repetition.
     */
    private OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getItems().stream()
                        .map(OrderItem::getName)
                        .collect(Collectors.toList())
        );
    }
}
