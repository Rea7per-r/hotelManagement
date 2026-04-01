package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/book")
    public String createBooking(@RequestParam Long customerId,@RequestParam Long roomId,
                                @RequestParam LocalDate checkIn,
                                @RequestParam LocalDate checkOut,
                                @RequestParam int guests)
    {
        bookingService.createBooking(customerId, roomId, checkIn, checkOut, guests);

                return "Booked Successfully";
    }

    @GetMapping("/allBookings")
    public List<Booking> getBookings()
    {
        return bookingService.getAll();
    }
}
