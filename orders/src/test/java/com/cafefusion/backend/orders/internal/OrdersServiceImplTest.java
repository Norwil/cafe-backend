package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cafefusion.backend.menu.api.model.MenuItemDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceImplTest {

    @Mock
    private MenuApi menuApi;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrdersServiceImpl ordersService;

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
        Order order = new Order(Instant.now(), new BigDecimal("12.50"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem(order, 1L, "Cappuccino", new BigDecimal("12.50"));
        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<OrderDto> result = ordersService.getOrderById(1L);

        // Assert & Verify
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().orderId());
        assertEquals(OrderStatus.PENDING, result.get().status());
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


}
