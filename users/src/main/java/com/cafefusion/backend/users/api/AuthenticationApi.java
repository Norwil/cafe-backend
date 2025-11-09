package com.cafefusion.backend.users.api;


import com.cafefusion.backend.users.api.model.AuthenticationRequest;
import com.cafefusion.backend.users.api.model.AuthenticationResponse;
import com.cafefusion.backend.users.api.model.RegisterRequest;

/**
 * Public API for the Users Module, specifically for
 * authentication and registration
 */
public interface AuthenticationApi {

    /**
     * Registers a new user in the system.
     * @param request The registration details (name, email, password, role).
     * @return An AuthenticationResponse containing a new JWT.
     */
    AuthenticationResponse register(RegisterRequest request);

    /**
     * Authenticates an existing user.
     * @param request The login details (email, password).
     * @return An AuthenticationResponse containing a new JWT.
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

}
