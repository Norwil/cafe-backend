package com.cafefusion.backend.web;

import com.cafefusion.backend.config.SecurityConfig;
import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
@Import(SecurityConfig.class)
public class OrdersControllerTest {

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
    void createNewOrder_whenAnonymous_shouldReturnForbidden() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyHistory_whenLoggedInAAsUser_shouldReturnOrderList() throws Exception {
        // Arrange
        OrderDto fakeOrder = new OrderDto(
                1L, 2L, Instant.now(), OrderStatus.PENDING_APPROVAL, new BigDecimal("30.50"), List.of("Item A")
        );

        when(ordersApi.getMyOrderHistory()).thenReturn(List.of(fakeOrder));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/orders/my-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].userId").value(2L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createNewOrder_whenUserRole_shouldReturnCreated() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));
        // We must update the DTO to include the new userId and status fields
        OrderDto fakeResponse = new OrderDto(
                1L, 2L, Instant.now(), OrderStatus.PENDING_APPROVAL, new BigDecimal("12.50"), List.of("Cappuccino")
        );
        when(ordersApi.createOrder(any(CreateOrderRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.userId").value(2L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_whenAdminRole_shouldReturnOk() throws Exception {
        OrderDto fakeOrder = new OrderDto(
                1L, 1L, Instant.now(), OrderStatus.PENDING_APPROVAL, new BigDecimal("30.50"), List.of("Cappuccino", "Baklava")
        );
        when(ordersApi.getOrderById(1L)).thenReturn(Optional.of(fakeOrder));

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateOrderStatus_whenUserRole_shouldReturnForbidden() throws Exception {
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.COMPLETED);

        mockMvc.perform(put("/api/v1/orders/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


}
