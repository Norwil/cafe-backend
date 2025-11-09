package com.cafefusion.backend.users.api.model;

/**
 * public DTO for a login request.
 *
 * @param email
 * @param password
 */
public record AuthenticationRequest(
        String email,
        String password
) {
}
