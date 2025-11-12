package com.cafefusion.backend.users.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Public DTO for a registration request.
 */
public record RegisterRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotNull(message = "Role is required")
        Role role
) {
}
