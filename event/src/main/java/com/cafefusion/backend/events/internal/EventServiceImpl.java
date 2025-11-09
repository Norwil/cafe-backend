package com.cafefusion.backend.events.internal;

import com.cafefusion.backend.events.api.EventApi;
import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventApi {

    private final EventRepository eventRepository;

    private static final ZoneId CAFE_TIMEZONE = ZoneId.of("Europe/Warsaw");

    @Override
    @Transactional
    public EventDto createEvent(CreateEventRequest request) {
        log.info("Creating new event: {}", request.name());

        Event newEvent = new Event(
                request.name(),
                request.description(),
                request.eventDateTime(),
                request.coverCharge()
        );

        Event savedEvent = eventRepository.save(newEvent);
        return toDto(savedEvent);
    }

    @Override
    @Transactional
    public Optional<EventDto> getEventById(Long eventId) {
        log.info("Fetching event by ID: {}", eventId);
        return eventRepository.findById(eventId)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getUpcomingEvents() {
        log.info("Fetching upcoming events");

        ZonedDateTime now = ZonedDateTime.now(CAFE_TIMEZONE);

        return eventRepository.findUpcomingEvents(now).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(Long eventId) {
        log.info("Deleting event by ID: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Event not found: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    /**
     * Private helper method to map the Event entity to its DTO
     */
    private EventDto toDto(Event event) {
        return new EventDto(
            event.getId(),
            event.getName(),
            event.getDescription(),
            event.getEventDateTime(),
            event.getCoverCharge()
        );
    }
}
