package com.suraj.hotelManagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Disable filters → NO security applied
@WebMvcTest(TestController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class TestControllerTest {

    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;
    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;



    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Access Granted"));
    }

    @Test
    void shouldAccessReceptionEndpoint() throws Exception {
        mockMvc.perform(get("/reception/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reception Access Granted"));
    }

    @Test
    void shouldAccessCustomerEndpoint() throws Exception {
        mockMvc.perform(get("/customer/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer Access Granted"));
    }
}