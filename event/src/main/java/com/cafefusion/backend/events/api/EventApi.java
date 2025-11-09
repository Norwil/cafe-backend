package com.cafefusion.backend.events.api;

import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;

import java.util.List;
import java.util.Optional;

public interface EventApi {

    /**
     * Creates a new event
     * @param request The request DTO containing event details.
     * @return The newly created event as a DTO.
     */
    EventDto createEvent(CreateEventRequest request);

    /**
     * Finds a single event by its unique ID.
     * @param eventId The ID of the event to find.
     * @return An Optional containing the EventDto if found.
     */
    Optional<EventDto> getEventById(Long eventId);

    /**
     * Retrieves a lsit of all upcoming events.
     * typically ordered by date.
     * @return A list of upcoming EventDtos
     */
    List<EventDto> getUpcomingEvents();

    /**
     * Deletes an event by its ID.
     *
     * @param eventId The ID of the event to delete.
     */
    void deleteEvent(Long eventId);

}
