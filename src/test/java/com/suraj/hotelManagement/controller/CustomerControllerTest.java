package com.suraj.hotelManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.hotelManagement.dto.RegisterRequestDTO;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.service.CustomerService;
import com.suraj.hotelManagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {
    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;
    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // TEST: Add Customer
    @Test
    void shouldAddCustomerSuccessfully() throws Exception {

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("Suraj");

        doNothing().when(customerService).save(any(Customer.class));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("saved customer successfully"));

        verify(customerService, times(1)).save(any(Customer.class));
    }

    // TEST: Get All Customers
    @Test
    void shouldReturnAllCustomers() throws Exception {

        Customer c1 = new Customer();
        c1.setCustomerId(1L);
        c1.setName("Suraj");

        Customer c2 = new Customer();
        c2.setCustomerId(2L);
        c2.setName("Aryan");

        when(customerService.getAll()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/customers/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(customerService, times(1)).getAll();
    }

    //  TEST: Register Customer
    @Test
    void shouldRegisterCustomerSuccessfully() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("test@gmail.com");
        request.setPassword("1234");

        when(userService.registerCustomer(anyString(), anyString()))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userService, times(1))
                .registerCustomer("test@gmail.com", "1234");
    }
}