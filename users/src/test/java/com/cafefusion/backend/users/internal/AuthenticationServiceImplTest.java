package com.cafefusion.backend.users.internal;

import com.cafefusion.backend.users.api.model.AuthenticationRequest;
import com.cafefusion.backend.users.api.model.AuthenticationResponse;
import com.cafefusion.backend.users.api.model.RegisterRequest;
import com.cafefusion.backend.users.api.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void register_shouldSaveUserAndReturnTaken() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test", "User", "test@example.com", "password123", Role.USER);

        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("fake.jwt.token");

        // Act
        AuthenticationResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.token());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("hashedpassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password123");

        User fakeUser = User.builder()
                .email("user@example.com")
                .password("hashedpassword")
                .role(Role.USER)
                .build();

        // Mock
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(fakeUser));
        when(jwtService.generateToken(fakeUser)).thenReturn("fake.jwt.token");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert & Verify
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.token());

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void authenticate_shouldThrowException_whenCredentialsAreInvalid() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "wrongpassword");

        // Mock
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Assert & Verify
        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(request);
        });

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
