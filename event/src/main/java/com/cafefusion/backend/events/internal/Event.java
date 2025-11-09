package com.cafefusion.backend.events.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "events", schema = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000) // Allow for a longer description
    private String description;

    @Column(nullable = false)
    private ZonedDateTime eventDateTime;

    @Column(nullable = false)
    private BigDecimal coverCharge;

    public Event(String name, String description, ZonedDateTime eventDateTime, BigDecimal coverCharge) {
        this.name = name;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.coverCharge = coverCharge;
    }
}
