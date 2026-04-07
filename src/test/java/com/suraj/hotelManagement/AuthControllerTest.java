package com.suraj.hotelManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.hotelManagement.controller.AuthController;
import com.suraj.hotelManagement.dto.AuthRequestDTO;
import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.model.enums.Role;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)

@AutoConfigureMockMvc(addFilters = false)

class AuthControllerTest {

    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc; //for simulating http reqs

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. SUCCESS TEST
    @Test
    void shouldLoginSuccessfully() throws Exception {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("suraj");
        request.setPassword("password");

        User user = new User();
        user.setUsername("suraj");
        user.setRole(Role.CUSTOMER);

        // Mock authentication (no exception = success)
        when(authManager.authenticate(any()))
                .thenReturn(null);

        // Mock DB
        when(userRepository.findByUsername("suraj"))
                .thenReturn(Optional.of(user));

        // Mock JWT
        when(jwtUtil.generateToken("suraj", "CUSTOMER"))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked-jwt-token"));
    }

    // 2. AUTHENTICATION FAILURE
    @Test
    void shouldFailWhenAuthenticationFails() throws Exception {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("suraj");
        request.setPassword("wrong");

        // Throw exception when authenticate is called
        doThrow(new RuntimeException("Bad credentials"))
                .when(authManager).authenticate(any());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // 3. USER NOT FOUND AFTER AUTH
    @Test
    void shouldFailWhenUserNotFound() throws Exception {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("suraj");
        request.setPassword("password");

        when(authManager.authenticate(any()))
                .thenReturn(null);

        // User not found
        when(userRepository.findByUsername("suraj"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}