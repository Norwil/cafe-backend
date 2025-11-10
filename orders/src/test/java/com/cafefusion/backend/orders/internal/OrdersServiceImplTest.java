package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.orders.api.exception.InvalidStatusTransitionException;
import com.cafefusion.backend.orders.api.exception.OrderNotFoundException;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import com.cafefusion.backend.users.api.model.Role;
import com.cafefusion.backend.users.internal.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cafefusion.backend.menu.api.model.MenuItemDto;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrdersServiceImplTest {

    @Mock
    private MenuApi menuApi;
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrdersServiceImpl ordersService;

    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = User.builder()
                .id(2L)
                .email("user@cafefusion.com")
                .role(Role.USER)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(fakeUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createOrder_shouldSaveOrderWithCorrectUserId() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));
        MenuItemDto cappuccino = new MenuItemDto(1L, "Cappuccino", "Coffee", new BigDecimal("12.50"));

        when(menuApi.getMenuItemById(1L)).thenReturn(Optional.of(cappuccino));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderDto result = ordersService.createOrder(request);

        // Assert & Verify
        assertNotNull(result);
        assertEquals(2L, result.userId());
        assertEquals(new BigDecimal("12.50"), result.totalPrice());
        assertEquals(OrderStatus.PENDING_APPROVAL, result.status());

        // Capture the order that was saved
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(2L, orderCaptor.getValue().getUserId());
    }

    @Test
    void getMyOrderHistory_shouldReturnOrdersForCorrectUser() {
        // Arrange
        Order order1 = new Order(2L, Instant.now(), new BigDecimal("12.50"));
        order1.setItems(List.of(new OrderItem(order1, 1L, "Cappuccino", new BigDecimal("12.50"))));

        when(orderRepository.findByUserIdOrderByCreatedAtDesc(2L)).thenReturn(List.of(order1));

        // Act
        List<OrderDto> results = ordersService.getMyOrderHistory();

        // Verify & Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).userId());

        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc(2L);
        verify(orderRepository, never()).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void createOrder_shouldSucceed_whenItemsAreValid() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(List.of(1L, 2L));

        MenuItemDto cappuccino = new MenuItemDto(1L, "Cappuccino", "Coffee", new BigDecimal("12.50"));
        MenuItemDto baklava = new MenuItemDto(2L, "Baklava", "Dessert", new BigDecimal("18.00"));

        when(menuApi.getMenuItemById(1L)).thenReturn(Optional.of(cappuccino));
        when(menuApi.getMenuItemById(2L)).thenReturn(Optional.of(baklava));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        //  Assert & Verify
        OrderDto result = ordersService.createOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.orderId());
        assertEquals(new BigDecimal("30.50"), result.totalPrice());
        assertEquals(2, result.itemNames().size());
        assertTrue(result.itemNames().contains("Cappuccino"));
        assertTrue(result.itemNames().contains("Baklava"));

        verify(menuApi, times(1)).getMenuItemById(1L);
        verify(menuApi, times(1)).getMenuItemById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() {
        // Arrange
        Order order = new Order(2L, Instant.now(), new BigDecimal("12.50"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING_APPROVAL);

        OrderItem item = new OrderItem(order, 1L, "Cappuccino", new BigDecimal("12.50"));
        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<OrderDto> result = ordersService.getOrderById(1L);

        // Assert & Verify
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().orderId());
        assertEquals(OrderStatus.PENDING_APPROVAL, result.get().status());
        assertEquals(1, result.get().itemNames().size());
        assertEquals("Cappuccino", result.get().itemNames().get(0));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void createOrder_shouldThrowException_whenItemNotFound() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(List.of(99L));

        when(menuApi.getMenuItemById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ordersService.createOrder(request);
        });

        assertEquals("Item not found: 99", exception.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrders_shouldReturnPagedOrders() {
        Order order = new Order(2L, Instant.now(), new BigDecimal("10"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING_APPROVAL);
        Page<Order> page = new PageImpl<>(List.of(order));
        order.setItems(List.of(new OrderItem(order, 1L, "Cappuccino", new BigDecimal("10"))));


        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<OrderDto> result = ordersService.getOrders(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(order.getId(), result.getContent().get(0).orderId());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void getOrdersByStatuses_shouldReturnFilteredPagedOrders() {
        Order order1 = new Order(2L, Instant.now(), new BigDecimal("10"));
        order1.setId(1L);
        order1.setStatus(OrderStatus.IN_PROGRESS);
        order1.setItems(List.of(new OrderItem(order1, 1L, "Cappuccino", new BigDecimal("10"))));


        List<OrderStatus> statuses = List.of(OrderStatus.IN_PROGRESS);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order1));

        when(orderRepository.findByStatusIn(statuses, pageable)).thenReturn(page);

        Page<OrderDto> result = ordersService.getOrdersByStatuses(statuses, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(OrderStatus.IN_PROGRESS, result.getContent().get(0).status());
        verify(orderRepository).findByStatusIn(statuses, pageable);
    }

    @Test
    void getOrderStatistics_shouldReturnCountsPerStatus() {
        for (OrderStatus status : OrderStatus.values()) {
            // stub the method actually called when start/end are null
            when(orderRepository.countByStatus(status)).thenReturn((long) status.ordinal() + 1);
        }

        Map<OrderStatus, Long> stats = ordersService.getOrderStatistics(null, null);

        for (OrderStatus status : OrderStatus.values()) {
            assertEquals((long) status.ordinal() + 1, stats.get(status));
            verify(orderRepository).countByStatus(status); // verify correct method
        }
    }

    @Test
    void getOrderStatistics_withDateRange_shouldReturnCountsPerStatus() {
        Instant start = Instant.parse("2025-11-01T00:00:00Z");
        Instant end = Instant.parse("2025-11-30T23:59:59Z");

        for (OrderStatus status : OrderStatus.values()) {
            when(orderRepository.countByStatusAndCreatedAtBetween(status, start, end))
                    .thenReturn((long) status.ordinal() + 10);
        }

        Map<OrderStatus, Long> stats = ordersService.getOrderStatistics(start, end);

        for (OrderStatus status : OrderStatus.values()) {
            assertEquals((long) status.ordinal() + 10, stats.get(status));
            verify(orderRepository).countByStatusAndCreatedAtBetween(status, start, end);
        }
    }

    @Test
    void getKitchenOrders_shouldReturnInProgressAndReadyForPickup() {
        Order order = new Order(2L, Instant.now(), new BigDecimal("10"));
        order.setId(1L);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setItems(List.of(new OrderItem(order, 1L, "Cappuccino", new BigDecimal("10"))));


        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findByStatusIn(List.of(OrderStatus.IN_PROGRESS, OrderStatus.READY_FOR_PICKUP), pageable))
                .thenReturn(page);

        Page<OrderDto> result = ordersService.getKitchenOrders(pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(List.of(OrderStatus.IN_PROGRESS, OrderStatus.READY_FOR_PICKUP)
                .contains(result.getContent().get(0).status()));
    }

    @Test
    void updateStatusValidated_shouldUpdateValidTransition() {
        Order order = new Order(2L, Instant.now(), new BigDecimal("10"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING_APPROVAL);
        order.setItems(List.of(new OrderItem(order, 1L, "Cappuccino", new BigDecimal("10"))));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);

        OrderDto result = ordersService.updateStatusValidated(1L, request);

        assertEquals(OrderStatus.CONFIRMED, result.status());
        verify(orderRepository).findById(1L);
    }

    @Test
    void updateStatusValidated_shouldThrowForInvalidTransition() {
        Order order = new Order(2L, Instant.now(), new BigDecimal("10"));
        order.setId(1L);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);

        assertThrows(InvalidStatusTransitionException.class,
                () -> ordersService.updateStatusValidated(1L, request));
    }

    @Test
    void updateStatusValidated_shouldThrowIfOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);

        assertThrows(OrderNotFoundException.class,
                () -> ordersService.updateStatusValidated(999L, request));
    }



}
