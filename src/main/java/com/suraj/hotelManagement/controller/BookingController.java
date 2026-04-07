package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.BookingIdDTO;
import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.dto.BookingResponseDTO;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

        log.info("Create booking request | customerId={} | roomId={}",
                request.getCustomerId(), request.getRoomId());

        BookingResponseDTO response = bookingService.createBooking(request);

        log.info("Booking created successfully | bookingId={}", response.getBookingId());

        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','CUSTOMER')")
    @PostMapping("/cancel")
    public BookingResponseDTO cancelBooking(@RequestBody BookingIdDTO request) {

        log.info("Cancel booking request | bookingId={}", request.getBookingId());

        BookingResponseDTO response = bookingService.cancelBooking(request.getBookingId());

        log.info("Booking cancelled successfully | bookingId={}", request.getBookingId());

        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @GetMapping("/allBookings")
    public List<Booking> getBookings() {

        log.info("Fetch all bookings request");

        List<Booking> bookings = bookingService.getAll();

        log.info("Fetched {} bookings", bookings.size());

        return bookings;
    }

    @PostMapping("/myBookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Booking> getMyBookings(Authentication authentication) {

        String username = authentication.getName();

        log.info("Fetch bookings for user={}", username);

        List<Booking> bookings = bookingService.getByUsername(username);

        log.info("Fetched {} bookings for user={}", bookings.size(), username);

        return bookings;
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestBody BookingIdDTO request) {

        Long bookingId = request.getBookingId();

        log.info("Check-in request | bookingId={}", bookingId);

        bookingService.checkIn(bookingId);

        log.info("Check-in successful | bookingId={}", bookingId);

        return "Checked in successfully";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestBody BookingIdDTO request) {

        Long bookingId = request.getBookingId();

        log.info("Check-out request | bookingId={}", bookingId);

        bookingService.checkOut(bookingId);

        log.info("Check-out successful | bookingId={}", bookingId);

        return "Checked out successfully";
    }
}