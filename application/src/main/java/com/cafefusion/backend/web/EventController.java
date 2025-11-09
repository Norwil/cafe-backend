package com.cafefusion.backend.web;

import com.cafefusion.backend.events.api.EventApi;
import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventApi eventApi;

    @GetMapping
    public List<EventDto> getUpcomingEvents() {
        return eventApi.getUpcomingEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable("id") Long eventId) {
        return eventApi.getEventById(eventId)
                .map(eventDto -> ResponseEntity.ok(eventDto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public EventDto createNewEvent(@RequestBody CreateEventRequest request) {
        return eventApi.createEvent(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventApi.deleteEvent(eventId);
    }
}
