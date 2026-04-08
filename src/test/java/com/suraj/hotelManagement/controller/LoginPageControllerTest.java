package com.suraj.hotelManagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginPageController.class)
@AutoConfigureMockMvc(addFilters = false) //  disable security
class LoginPageControllerTest {
    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;
    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;



    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnLoginPage() throws Exception {

        mockMvc.perform(get("/loginPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("login")); // checks returned template
    }
}