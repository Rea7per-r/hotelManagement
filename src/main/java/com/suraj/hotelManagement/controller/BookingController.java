package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.BookingIdDTO;
import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/book")
    public Booking createBooking(@RequestBody BookingRequestDTO request) {
            return bookingService.createBooking(request);


    }

    @PostMapping("/cancel")
    public Booking cancelBooking(@RequestBody BookingIdDTO request)
    {
        return bookingService.cancelBooking(request.getBookingId());
    }


    @GetMapping("/allBookings")
    public List<Booking> getBookings()
    {
        return bookingService.getAll();
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
