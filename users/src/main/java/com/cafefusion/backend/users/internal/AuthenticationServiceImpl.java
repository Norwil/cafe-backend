package com.cafefusion.backend.users.internal;

import com.cafefusion.backend.users.api.AuthenticationApi;
import com.cafefusion.backend.users.api.model.AuthenticationRequest;
import com.cafefusion.backend.users.api.model.AuthenticationResponse;
import com.cafefusion.backend.users.api.model.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationApi {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Create a new User object from the request
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        // Save the new User to the database
        userRepository.save(user);

        // Generate a JWT for the new user
        var jwtToken = jwtService.generateToken(user);

        // Return the token in the response
        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);

    }
}
