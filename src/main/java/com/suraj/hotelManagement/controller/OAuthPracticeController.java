package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.AuthorizeRequestDTO;
import com.suraj.hotelManagement.dto.TokenRequestDTO;
import com.suraj.hotelManagement.model.enums.Role;
import com.suraj.hotelManagement.security.JwtUtil;
import com.suraj.hotelManagement.security.PkceStore;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class OAuthPracticeController {

    private static final Logger log = LoggerFactory.getLogger(OAuthPracticeController.class);

    @Autowired
    private PkceStore pkceStore;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // AUTHORIZE
    @PostMapping("/authorize")
    public String authorize(@RequestBody AuthorizeRequestDTO request) {

        log.info("Authorize request received for username={}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found for username={}", request.getUsername());
                    return new RuntimeException("User not found");
                });

        String authCode = UUID.randomUUID().toString();

        log.debug("Generated authCode={} for username={}", authCode, user.getUsername());

        pkceStore.storeCodeChallenge(authCode, request.getCodeChallenge());
        pkceStore.storeUser(authCode, user.getUsername());

        log.info("Auth code stored successfully for username={}", user.getUsername());

        return authCode;
    }

    // TOKEN
    @PostMapping("/token")
    public String token(@RequestBody TokenRequestDTO request) {

        log.info("Token request received for authCode={}", request.getAuthCode());

        String storedChallenge = pkceStore.getCodeChallenge(request.getAuthCode());
        String username = pkceStore.getUsername(request.getAuthCode());

        if (storedChallenge == null || username == null) {
            log.error("Invalid authCode={} - no stored data found", request.getAuthCode());
            throw new RuntimeException("Invalid auth code");
        }

        String generatedChallenge =
                Integer.toHexString(request.getCodeVerifier().hashCode());

        log.debug("Comparing PKCE challenge: stored={} generated={}",
                storedChallenge, generatedChallenge);

        if (!generatedChallenge.equals(storedChallenge)) {
            log.error("PKCE verification failed for authCode={}", request.getAuthCode());
            throw new RuntimeException("Invalid PKCE verification");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found during token generation username={}", username);
                    return new RuntimeException("User not found");
                });

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        log.info("JWT token generated successfully for username={} role={}",
                user.getUsername(), user.getRole());

        pkceStore.remove(request.getAuthCode());

        log.debug("Auth code={} removed from PKCE store", request.getAuthCode());

        return token;
    }
}