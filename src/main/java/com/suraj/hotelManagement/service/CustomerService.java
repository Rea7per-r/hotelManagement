package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private BookingRepository bookingRepository;


    public void save(Customer customer) {
        repo.save(customer);

    }

    public List<Customer> getAll() {
        return repo.findAll();
    }

}
