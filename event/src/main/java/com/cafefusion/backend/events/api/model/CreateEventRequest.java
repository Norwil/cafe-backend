package com.cafefusion.backend.events.api.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CreateEventRequest(
        String name,
        String description,
        ZonedDateTime eventDateTime,
        BigDecimal coverCharge
) {
}
