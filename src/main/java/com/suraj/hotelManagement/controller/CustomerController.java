package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.RegisterRequestDTO;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.service.CustomerService;
import com.suraj.hotelManagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {


    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private UserService userService;

    public CustomerController(CustomerService service) {
        this.service = service;
    }


    @Autowired
    private CustomerService service;


    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','CUSTOMER')")
    @PostMapping("/add")
    public String addCustomer(@RequestBody Customer customer)
    {
        service.save(customer);
        return "saved customer successfully";
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @GetMapping("/get")
    public List<Customer> getCustomer()
    {
        return service.getAll();
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
