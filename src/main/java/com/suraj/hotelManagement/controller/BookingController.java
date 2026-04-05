package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.BookingIdDTO;
import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.dto.BookingResponseDTO;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/booking")
public class BookingController {
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    BookingService bookingService;


    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','CUSTOMER')")
    @PostMapping("/book")
    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO request) {
        log.info("Received booking request | customerId={} | roomId={}",
                request.getCustomerId(), request.getRoomId());
            return bookingService.createBooking(request);


    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','CUSTOMER')")
    @PostMapping("/cancel")
    public BookingResponseDTO cancelBooking(@RequestBody BookingIdDTO request)
    {
        return bookingService.cancelBooking(request.getBookingId());
    }


    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @GetMapping("/allBookings")
    public List<Booking> getBookings()
    {
        return bookingService.getAll();
    }




    @PostMapping("/myBookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Booking> getMyBookings(Authentication auth) {

        String username = auth.getName();

        return bookingService.getByUsername(username);
    }



    @PostMapping("/checkin")
    public String checkIn(@RequestBody BookingIdDTO request) {
        Long bookingId=request.getBookingId();
        bookingService.checkIn(bookingId);
        return "Checked in successfully";

    }

    @PostMapping("/checkout")
    public String checkout(@RequestBody BookingIdDTO request){
        Long bookingId=request.getBookingId();
        bookingService.checkOut(bookingId);
        return "Checked out successfully";
    }


}
