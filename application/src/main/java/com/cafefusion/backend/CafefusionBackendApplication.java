package com.cafefusion.backend;

import com.cafefusion.backend.orders.api.OrdersApi;
import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class CafefusionBackendApplication implements CommandLineRunner {

    private final MenuApi menuApi;
    private final OrdersApi ordersApi;

    public static void main(String[] args) {
        SpringApplication.run(CafefusionBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- (1) TESTING MENU API ---");

        // Test the MenuApi
        Optional<MenuItemDto> item = menuApi.getMenuItemById(1L);
        if (item.isPresent()) {
            log.info("Found item 1: {}", item.get());
        }

        log.info("--- (2) TESTING ORDERS API (CROSS-MODULE CALL) ---");

        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(1L, 2L));

        // Call the OrdersApi!
        OrderDto newOrder = ordersApi.createOrder(orderRequest);

        log.info("Successfully created new order: {}", newOrder);

        log.info("--- ALL TESTS COMPLETE ---");
    }

}