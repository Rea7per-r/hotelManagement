package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.AuthRequestDTO;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.suraj.hotelManagement.model.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDTO request) {

        log.info("Login attempt for username={}", request.getUsername());

        try {
            // Authenticate user
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            log.info("Authentication successful for username={}", request.getUsername());

            // Fetch user
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("User not found in DB after authentication | username={}", request.getUsername());
                        return new RuntimeException("User not found");
                    });

            log.info("User fetched successfully | username={} | role={}",
                    user.getUsername(), user.getRole());

            // Generate token
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name()
            );

            log.info("JWT token generated successfully for username={}", user.getUsername());

            return token;

        } catch (Exception e) {
            log.error("Authentication failed for username={} | reason={}",
                    request.getUsername(), e.getMessage());
            throw e;
        }



    }
}