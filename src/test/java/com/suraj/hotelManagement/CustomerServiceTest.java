package com.suraj.hotelManagement;

import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    BookingRepository bookingRepository;

    @Test
    void shouldSaveCustomerSuccessfully() {

        Customer customer = new Customer();
        customer.setName("Suraj");

        when(customerRepository.save(customer)).thenReturn(customer);


        customerService.save(customer);

        verify(customerRepository, times(1)).save(customer);
    }
}