package com.cafefusion.backend.web;


import com.cafefusion.backend.config.SecurityConfig;
import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import com.cafefusion.backend.users.internal.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOrdersController.class)
@Import(SecurityConfig.class)
public class AdminOrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersApi ordersApi;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllOrders_shouldReturnPagedOrders() throws Exception {
        OrderDto orderDto = new OrderDto(1L, 2L, Instant.now(), OrderStatus.PENDING_APPROVAL,
                new BigDecimal("10.00"), List.of("Cappuccino"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDto> page = new PageImpl<>(List.of(orderDto), pageable, 1);

        when(ordersApi.getOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(1));

        verify(ordersApi, times(1)).getOrders(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getOrdersByStatus_shouldReturnFilteredOrders() throws Exception {
        OrderDto orderDto = new OrderDto(1L, 2L, Instant.now(), OrderStatus.IN_PROGRESS,
                new BigDecimal("10.00"), List.of("Cappuccino"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDto> page = new PageImpl<>(List.of(orderDto), pageable, 1);

        when(ordersApi.getOrdersByStatuses(anyList(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/orders/filter")
                        .param("statuses", "IN_PROGRESS", "READY_FOR_PICKUP")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));

        verify(ordersApi, times(1)).getOrdersByStatuses(anyList(), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getStats_shouldReturnMapOfOrderStatus() throws Exception {
        Map<OrderStatus, Long> stats = Map.of(
                OrderStatus.PENDING_APPROVAL, 5L,
                OrderStatus.COMPLETED, 2L
        );

        // Match the new method signature
        when(ordersApi.getOrderStatistics(null, null)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/orders/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDING_APPROVAL").value(5))
                .andExpect(jsonPath("$.COMPLETED").value(2));

        verify(ordersApi, times(1)).getOrderStatistics(null, null);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getKitchenOrders_shouldReturnKitchenOrders() throws Exception {
        OrderDto orderDto = new OrderDto(1L, 2L, Instant.now(), OrderStatus.IN_PROGRESS,
                new BigDecimal("10.50"), List.of("Cappuccino"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDto> page = new PageImpl<>(List.of(orderDto), pageable, 1);

        when(ordersApi.getKitchenOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/orders/kitchen")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));

        verify(ordersApi, times(1)).getKitchenOrders(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateStatus_shouldReturnUpdatedOrder() throws Exception {
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);
        OrderDto updatedOrder = new OrderDto(1L, 2L, Instant.now(), OrderStatus.CONFIRMED,
                new BigDecimal("10.00"), List.of("Cappuccino"));

        when(ordersApi.updateStatusValidated(eq(1L), any(UpdateOrderRequest.class)))
                .thenReturn(updatedOrder);

        mockMvc.perform(put("/api/v1/admin/orders/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(ordersApi, times(1)).updateStatusValidated(eq(1L), any(UpdateOrderRequest.class));
    }

}
