package com.cafefusion.backend.users.api.model;

/**
 * Public DTO for sending back a JWT token.
 */
public record AuthenticationResponse(
        String token
) {
}
