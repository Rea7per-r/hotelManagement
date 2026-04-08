package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.model.enums.Role;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.security.JwtUtil;
import com.suraj.hotelManagement.security.PkceStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthPracticeController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security
class OAuthPracticeControllerTest {
    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;

    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean private PkceStore pkceStore;
    @MockBean private UserRepository userRepository;

    // 1. AUTHORIZE SUCCESS
    @Test
    void shouldGenerateAuthCode() throws Exception {

        User user = new User();
        user.setUsername("test@example.com");

        when(userRepository.findByUsername("test@example.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/oauth/authorize")
                        .contentType("application/json")
                        .content("""
                            {
                              "username": "test@example.com",
                              "codeChallenge": "abc123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    //  2. AUTHORIZE FAIL (user not found)
    @Test
    void shouldFailAuthorizeWhenUserNotFound() throws Exception {

        when(userRepository.findByUsername("wrong@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/oauth/authorize")
                        .contentType("application/json")
                        .content("""
                            {
                              "username": "wrong@example.com",
                              "codeChallenge": "abc123"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    //  3. TOKEN SUCCESS
    @Test
    void shouldReturnJwtToken() throws Exception {
        String codeVerifier = "someVerifier";
        String challenge = Integer.toHexString(codeVerifier.hashCode());

        when(pkceStore.getCodeChallenge("auth123")).thenReturn(challenge);
        when(pkceStore.getUsername("auth123")).thenReturn("test@example.com");

        User user = new User();
        user.setUsername("test@example.com");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("test@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/oauth/token")
                        .contentType("application/json")
                        .content("""
                            {
                              "authCode": "auth123",
                              "codeVerifier": "someVerifier"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }

    //  4. TOKEN FAIL (invalid auth code)
    @Test
    void shouldFailTokenWhenInvalidAuthCode() throws Exception {

        when(pkceStore.getCodeChallenge("badCode")).thenReturn(null);
        when(pkceStore.getUsername("badCode")).thenReturn(null);

        mockMvc.perform(post("/oauth/token")
                        .contentType("application/json")
                        .content("""
                            {
                              "authCode": "badCode",
                              "codeVerifier": "verifier"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    //  5. TOKEN FAIL (PKCE mismatch)
    @Test
    void shouldFailTokenWhenPkceInvalid() throws Exception {
        String codeVerifier = "someVerifier";
        String challenge = Integer.toHexString(codeVerifier.hashCode());

        when(pkceStore.getCodeChallenge("auth123")).thenReturn(challenge);
        when(pkceStore.getUsername("auth123")).thenReturn("test@example.com");

        mockMvc.perform(post("/oauth/token")
                        .contentType("application/json")
                        .content("""
                            {
                              "authCode": "auth123",
                              "codeVerifier": "verifier"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }
}