package com.cafefusion.backend.users.api.model;

/**
 * Public DTO for a registration request.
 */
public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role
) {
}
