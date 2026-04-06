package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.AuthorizeRequestDTO;
import com.suraj.hotelManagement.dto.RegisterRequestDTO;
import com.suraj.hotelManagement.dto.TokenRequestDTO;
import com.suraj.hotelManagement.security.JwtUtil;
import com.suraj.hotelManagement.security.PkceStore;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private PkceStore pkceStore;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);


    @PostMapping("/authorize")
    public String authorize(@RequestBody AuthorizeRequestDTO request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String authCode = UUID.randomUUID().toString();

        pkceStore.storeCodeChallenge(authCode, request.getCodeChallenge());
        pkceStore.storeUser(authCode, user.getUsername());

        return authCode;
    }


    @PostMapping("/token")
    public String token(@RequestBody TokenRequestDTO request) {

        String storedChallenge = pkceStore.getCodeChallenge(request.getAuthCode());
        String username = pkceStore.getUsername(request.getAuthCode());

        if (storedChallenge == null || username == null) {
            throw new RuntimeException("Invalid auth code");
        }

        String generatedChallenge =
                Integer.toHexString(request.getCodeVerifier().hashCode());

        if (!generatedChallenge.equals(storedChallenge)) {
            throw new RuntimeException("Invalid PKCE verification");
        }

        User user = userRepository.findByUsername(username).orElseThrow();

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        pkceStore.remove(request.getAuthCode());

        return token;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDTO request) {

        log.info("Incoming registration request | email={}", request.getUsername());

        return userService.registerCustomer(
                request.getUsername(),
                request.getPassword()
        );
    }
}