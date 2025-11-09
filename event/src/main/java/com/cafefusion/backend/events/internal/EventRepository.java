package com.cafefusion.backend.events.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.eventDateTime >= :now ORDER BY e.eventDateTime ASC")
    List<Event> findUpcomingEvents(ZonedDateTime now);
}
