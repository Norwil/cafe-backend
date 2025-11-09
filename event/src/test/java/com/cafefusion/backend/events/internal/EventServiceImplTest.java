package com.cafefusion.backend.events.internal;


import com.cafefusion.backend.events.api.model.CreateEventRequest;
import com.cafefusion.backend.events.api.model.EventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void creatEvent_shouldSaveAndReturnDto() {
        // Arrange
        CreateEventRequest request = new CreateEventRequest(
                "Jam Session",
                "Feel the vibe, grab your instrument",
                ZonedDateTime.now().plusDays(10),
                new BigDecimal("0.0")
        );

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(1L);
            return event;
        });

        // When
        EventDto result = eventService.createEvent(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Jam Session", result.name());
        assertEquals(new BigDecimal("0.0"), result.coverCharge());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void getUpcomingEvents_shouldCallRepositoryAndReturnDtoList() {
        // Arrange
        Event fakeEvent = new Event(
                "Future DJ",
                "Music",
                ZonedDateTime.now().plusDays(5),
                BigDecimal.TEN
        );
        fakeEvent.setId(1L);

        when(eventRepository.findUpcomingEvents(any(ZonedDateTime.class))).thenReturn(List.of(fakeEvent));

        // When
        List<EventDto> results = eventService.getUpcomingEvents();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).id());
        assertEquals("Future DJ", results.get(0).name());

        verify(eventRepository, times(1)).findUpcomingEvents(any(ZonedDateTime.class));
    }

    @Test
    void deleteEvent_shouldThrowException_whenEventNotFound() {
        // Arrange
        when(eventRepository.existsById(99L)).thenReturn(false);

        // Act & Verify
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(99L);
        });

        assertEquals("Event not found: 99", exception.getMessage());

        verify(eventRepository, times(1)).existsById(99L);
        verify(eventRepository, never()).deleteById(anyLong());
    }
}
