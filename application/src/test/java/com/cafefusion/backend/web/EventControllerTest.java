package com.cafefusion.backend.web;

import com.cafefusion.backend.events.api.EventApi;
import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventApi eventApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUpcomingEvents_shouldReturnListOfEvents() throws Exception {
        // Given
        EventDto fakeEvet = new EventDto(
                1L,
                "DJ Night",
                "EDM",
                ZonedDateTime.now(),
                BigDecimal.TEN
        );

        when(eventApi.getUpcomingEvents()).thenReturn(List.of(fakeEvet));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("DJ Night"));
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() throws Exception {
        // Given
        when(eventApi.getEventById(99L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewEvent_shouldReturnCreatedEvent() throws Exception {
        // Given
        CreateEventRequest request = new CreateEventRequest(
                "New Event", "Desc", ZonedDateTime.now(), BigDecimal.ZERO
        );
        EventDto responseDto = new EventDto(
                1L, "New Event", "Desc", request.eventDateTime(), BigDecimal.ZERO
        );

        when(eventApi.createEvent(any(CreateEventRequest.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Send the request as JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Event"));
    }
}
