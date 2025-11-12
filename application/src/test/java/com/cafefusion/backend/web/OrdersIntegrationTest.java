package com.cafefusion.backend.web;

import com.cafefusion.backend.AbstractIntegrationTest;
import com.cafefusion.backend.orders.api.model.OrderStatus;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;
import com.cafefusion.backend.orders.internal.Order;
import com.cafefusion.backend.users.api.model.Role;
import com.cafefusion.backend.users.internal.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;

import com.cafefusion.backend.menu.internal.MenuItem;
import com.cafefusion.backend.menu.internal.MenuItemRepository;
import com.cafefusion.backend.users.internal.UserRepository;

import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.internal.OrderRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class OrdersIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Entities ---
    private User testUser;
    private User adminUser;
    private Order userOrder;
    private Order adminOrder;


    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    void setUp() {
        // Clean up
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        userRepository.deleteAll();

        // --- Setup Users ---
        this.testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("user@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        userRepository.save(this.testUser);

        this.adminUser = new User();
        adminUser.setFirstName("Test");
        adminUser.setLastName("Admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setRole(Role.ADMIN);
        userRepository.save(this.adminUser);

        // --- Setup Menu Item ---
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Cappuccino");
        menuItem.setDescription("Test Coffee");
        menuItem.setPrice(new BigDecimal("10.00"));
        menuItemRepository.save(menuItem);

        // --- Setup Orders ---
        this.userOrder = new Order(testUser.getId(), Instant.now(), new BigDecimal("10.00"));
        this.userOrder.setStatus(OrderStatus.PENDING_APPROVAL);
        orderRepository.save(this.userOrder);

        this.adminOrder = new Order(adminUser.getId(), Instant.now(), new BigDecimal("20.00"));
        this.adminOrder.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(this.adminOrder);

        // Flush all data to the DB before tests run
        userRepository.flush();
        menuItemRepository.flush();
        orderRepository.flush();

    }

    /**
     * Helper method to create a valid auth token for our testUser
     */
    private UsernamePasswordAuthenticationToken getUserAuthToken() {
        return new UsernamePasswordAuthenticationToken(
                this.testUser, null, this.testUser.getAuthorities()
        );
    }

    /**
     * Helper method to create a valid auth token for our adminUser
     */
    private UsernamePasswordAuthenticationToken getAdminAuthToken() {
        return new UsernamePasswordAuthenticationToken(
                this.adminUser, null, this.adminUser.getAuthorities()
        );
    }

    @Test
    void createNewOrder_shouldCreateOrderInDatabase() throws Exception {
        // Arrange
        long menuItemId = menuItemRepository.findAll().get(0).getId();
        CreateOrderRequest request = new CreateOrderRequest(List.of(menuItemId));

        long initialOrderCount = orderRepository.count();

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(getUserAuthToken())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.itemNames[0]").value("Cappuccino"))
                .andExpect(jsonPath("$.totalPrice").value(10.00));

        // Assert
        assertEquals(initialOrderCount + 1, orderRepository.count());
    }

    @Test
    void getMyHistory_shouldReturnOnlyUserOrders() throws Exception {
        // This tests the logic in `getMyOrderHistory()`

        mockMvc.perform(get("/api/v1/orders/my-history")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(getUserAuthToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)) // Should only be 1 order, not 2
                .andExpect(jsonPath("$[0].orderId").value(this.userOrder.getId()));
    }

    @Test
    void getOrderById_whenUserIsOwner_shouldReturnOrder() throws Exception {
        // This tests the `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")`

        mockMvc.perform(get("/api/v1/orders/" + this.userOrder.getId())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(getUserAuthToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(this.userOrder.getId()));
    }

    @Test
    void getOrderById_whenAdminViewsOtherOrder_shouldReturnOrder() throws Exception {
        // This also tests the `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")`

        mockMvc.perform(get("/api/v1/orders/" + this.userOrder.getId()) // Admin views USER's order
                        .with(SecurityMockMvcRequestPostProcessors.authentication(getAdminAuthToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(this.userOrder.getId()));
    }

    @Test
    void updateOrderStatus_whenUser_shouldReturnForbidden() throws Exception {
        // This tests the `@PreAuthorize("hasRole('ADMIN')")`
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);

        mockMvc.perform(put("/api/v1/orders/" + this.userOrder.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(getUserAuthToken()))) // Logged in as USER
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrderStatus_whenAdmin_shouldUpdateStatus() throws Exception {
        // This test the full admin flow for updating status
        UpdateOrderRequest request = new UpdateOrderRequest(OrderStatus.CONFIRMED);

        mockMvc.perform(put("/api/v1/orders/" + this.userOrder.getId() + "/status" )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(getAdminAuthToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Assert (Database Layer)
        Order updatedOrder = orderRepository.findById(this.userOrder.getId()).get();
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
    }

    @Test
    void deleteOrder_whenAdmin_shouldDeleteOrder() throws Exception {
        // This tests the `@PreAuthorize("hasRole('ADMIN')")`
        long initialCount = orderRepository.count(); // 2

        mockMvc.perform(delete("/api/v1/orders/" + this.userOrder.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(getAdminAuthToken())))
                .andExpect(status().isNoContent());

        // Assert (Database Layer)
        assertEquals(initialCount - 1, orderRepository.count());
        assertFalse(orderRepository.existsById(this.userOrder.getId()));
    }

}