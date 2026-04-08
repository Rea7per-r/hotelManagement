package com.suraj.hotelManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.hotelManagement.dto.BookingIdDTO;
import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.dto.BookingResponseDTO;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;

    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. CREATE BOOKING
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldCreateBooking() throws Exception {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(100L);

        when(bookingService.createBooking(any())).thenReturn(response);

        mockMvc.perform(post("/booking/book")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(100));
    }

    //  2. CANCEL BOOKING
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldCancelBooking() throws Exception {

        BookingIdDTO request = new BookingIdDTO();
        request.setBookingId(1L);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(1L);

        when(bookingService.cancelBooking(1L)).thenReturn(response);

        mockMvc.perform(post("/booking/cancel")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    // 3. GET ALL BOOKINGS
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllBookings() throws Exception {

        Booking booking = new Booking();
        booking.setBookingId(1L);

        when(bookingService.getAll()).thenReturn(List.of(booking));

        mockMvc.perform(get("/booking/allBookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(1));
    }

//    //  4. GET MY BOOKINGS
//    @Test
//    void shouldGetMyBookings() throws Exception {
//
//        // mock data
//        Booking booking = new Booking();
//        booking.setBookingId(2L);
//
//        when(bookingService.getByUsername("suraj"))
//                .thenReturn(List.of(booking));
//
//        // perform request WITH security user
//        mockMvc.perform(post("/booking/myBookings")
//                        .with(user("suraj").roles("CUSTOMER")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].bookingId").value(2));
//    }

    //  5. CHECK-IN
    @Test
    void shouldCheckIn() throws Exception {

        BookingIdDTO request = new BookingIdDTO();
        request.setBookingId(1L);

        doNothing().when(bookingService).checkIn(1L);

        mockMvc.perform(post("/booking/checkin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Checked in successfully"));
    }

    //  6. CHECK-OUT
    @Test
    void shouldCheckOut() throws Exception {

        BookingIdDTO request = new BookingIdDTO();
        request.setBookingId(1L);

        doNothing().when(bookingService).checkOut(1L);

        mockMvc.perform(post("/booking/checkout")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Checked out successfully"));
    }
}