package com.fakenews.detection.controller;

import com.fakenews.detection.dto.AuthResponse;
import com.fakenews.detection.dto.LoginRequest;
import com.fakenews.detection.dto.RegisterRequest;
import com.fakenews.detection.entity.User;
import com.fakenews.detection.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller exposing authentication endpoints for User Registration and Login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/auth/register
     * Registers a new account.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = userService.registerUser(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("userId", registeredUser.getId());
        response.put("name", registeredUser.getName());
        response.put("email", registeredUser.getEmail());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/auth/login
     * Validates user credentials and returns user profile details.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
