package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.AuthRequestDTO;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.security.JwtUtil;
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

    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDTO request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return token;
    }
}