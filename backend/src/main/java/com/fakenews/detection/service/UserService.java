package com.fakenews.detection.service;

import com.fakenews.detection.dto.AuthResponse;
import com.fakenews.detection.dto.LoginRequest;
import com.fakenews.detection.dto.RegisterRequest;
import com.fakenews.detection.entity.User;
import com.fakenews.detection.exception.InvalidCredentialsException;
import com.fakenews.detection.exception.ResourceNotFoundException;
import com.fakenews.detection.exception.UserAlreadyExistsException;
import com.fakenews.detection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service handling User Registration, Authentication, and Profile lookup.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * Validates that email is unique and hashes the password with BCrypt.
     */
    public User registerUser(RegisterRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new UserAlreadyExistsException("Email '" + cleanEmail + "' is already registered. Please login instead.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Authenticates a user with email and password.
     */
    public AuthResponse authenticate(LoginRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify BCrypt password (supports fallback to raw match for easy demo testing)
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword())
                || request.getPassword().equals(user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "Login successful"
        );
    }

    /**
     * Finds a user by ID or throws ResourceNotFoundException.
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
