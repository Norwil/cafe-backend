package com.cafefusion.backend.events.api.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record EventDto(
        Long id,
        String name,
        String description,
        ZonedDateTime eventDateTime,
        BigDecimal coverCharge
) {
}
