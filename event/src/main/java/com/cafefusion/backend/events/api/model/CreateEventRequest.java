package com.cafefusion.backend.events.api.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CreateEventRequest(
        @NotBlank(message = "event name is required")
        @Size(max = 150, message = "Event name must be 150 characters or less")
        String name,

        @Size(max = 500, message = "Description must be 500 characters or less")
        String description,

        @NotNull(message = "Event date and time are required")
        @Future(message = "Event must be scheduled for a future date and time")
        ZonedDateTime eventDateTime,

        @NotNull(message = "Cover charge is required (can be 0)")
        @PositiveOrZero(message = "Cover charge must be zero or a positive value")
        BigDecimal coverCharge
) {
}
