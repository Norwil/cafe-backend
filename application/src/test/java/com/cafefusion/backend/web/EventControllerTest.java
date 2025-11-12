package com.cafefusion.backend.web;

import com.cafefusion.backend.config.SecurityConfig;
import com.cafefusion.backend.events.api.EventApi;
import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;
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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventApi eventApi;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUpcomingEvents_shouldReturnListOfEvents() throws Exception {
        // This is a public endpoint, no @WithMockUser is needed.
        EventDto fakeEvent = new EventDto(
                1L, "DJ Night", "EDM", ZonedDateTime.now(), BigDecimal.TEN
        );
        when(eventApi.getUpcomingEvents()).thenReturn(List.of(fakeEvent));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("DJ Night"));
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() throws Exception {
        when(eventApi.getEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewEvent_whenAnonymous_shouldReturnForbidden() throws Exception {
        CreateEventRequest request = new CreateEventRequest(
                "New Event", "Desc", ZonedDateTime.now(), BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createNewEvent_whenUserRole_shouldReturnForbidden() throws Exception {
        CreateEventRequest request = new CreateEventRequest(
                "New Event", "Desc", ZonedDateTime.now().plusDays(1), BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createNewEvent_whenAdminRole_shouldReturnCreated() throws Exception {
        CreateEventRequest request = new CreateEventRequest(
                "New Event", "Desc", ZonedDateTime.now().plusDays(1), BigDecimal.ZERO
        );
        EventDto responseDto = new EventDto(
                1L, "New Event", "Desc", request.eventDateTime(), BigDecimal.ZERO
        );

        when(eventApi.createEvent(any(CreateEventRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

}
