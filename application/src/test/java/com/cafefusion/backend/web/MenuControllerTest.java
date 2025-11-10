package com.cafefusion.backend.web;

import com.cafefusion.backend.config.SecurityConfig;
import com.cafefusion.backend.menu.api.MenuApi;
import com.cafefusion.backend.menu.api.model.CreateMenuItemRequest;
import com.cafefusion.backend.menu.api.model.MenuItemDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
@Import(SecurityConfig.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuApi menuApi;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- TEST 1: ANONYMOUS User (No Login) ---
    @Test
    void createMenuItem_whenAnonymous_shouldReturnUnauthorized() throws Exception {
        CreateMenuItemRequest request = new CreateMenuItemRequest("Test Item", "Desc", BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- TEST 2: Logged-in USER (Wrong Role) ---
    @Test
    @WithMockUser(roles = "USER")
    void createMenuItem_whenUserRole_shouldReturnForbidden() throws Exception {
        CreateMenuItemRequest request = new CreateMenuItemRequest("Test Item", "Desc", BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- TEST 3: Logged-in ADMIN (Correct Role) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void createMenuItem_whenAdminRole_shouldReturnOk() throws Exception {

        CreateMenuItemRequest request = new CreateMenuItemRequest("Admin Item", "Desc", BigDecimal.TEN);
        MenuItemDto responseDto = new MenuItemDto(1L, "Admin Item", "Desc", BigDecimal.TEN);

        when(menuApi.createMenuItem(any(CreateMenuItemRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Admin Item"));
    }
}