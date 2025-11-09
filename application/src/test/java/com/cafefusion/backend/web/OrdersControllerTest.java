package com.cafefusion.backend.web;

import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdersApi ordersApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrderById_shouldReturnOrder_whenFound() throws Exception {
        // Arrange
        OrderDto fakeOrder = new OrderDto(
                1L, Instant.now(), OrderStatus.PENDING, new BigDecimal("30.50"), List.of("Cappucino", "Baklava")
        );

        when(ordersApi.getOrderById(1L)).thenReturn(Optional.of(fakeOrder));

        // When and Then
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(30.50));
    }

    @Test
    void getOrderById_shouldReturnNotFound_whenMissing() throws Exception {
        // Arrange
        mockMvc.perform(get("/api/v1/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewOrder_shouldReturnCreatedOrder() throws  Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));
        OrderDto fakeResponse = new OrderDto(
                1L, Instant.now(), OrderStatus.PENDING, new BigDecimal("12.50"), List.of("Cappuccino")
        );

        when(ordersApi.createOrder(any(CreateOrderRequest.class))).thenReturn(fakeResponse);

        // When and Then
        mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
