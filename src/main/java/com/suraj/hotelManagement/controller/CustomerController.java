package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Autowired
    private CustomerService service;

    @PostMapping("/add")
    public String addCustomer(@RequestBody Customer customer)
    {
        service.save(customer);
        return "saved customer successfully";
    }


    @GetMapping("/get")
    public List<Customer> getCustomer()
    {
        return service.getAll();
    }






}
