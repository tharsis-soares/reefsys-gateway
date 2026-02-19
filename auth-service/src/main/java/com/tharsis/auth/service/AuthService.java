package com.tharsis.auth.service;

import com.tharsis.auth.dto.*;
import com.tharsis.auth.entity.User;
import com.tharsis.auth.repository.UserRepository;
import com.tharsis.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Auth Service
 *
 * Business logic for authentication and registration
 *
 * @author Tharsis Soares
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        user = userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
