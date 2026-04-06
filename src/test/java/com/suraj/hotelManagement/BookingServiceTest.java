package com.suraj.hotelManagement;

import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.exception.BadRequestException;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.model.enums.RoomStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.repository.RoomRepository;
import com.suraj.hotelManagement.service.BookingService;
import com.suraj.hotelManagement.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private RoomRepository roomRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private BookingService bookingService;

    // 1. SUCCESS CASE
    @Test
    void shouldCreateBookingSuccessfully() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(2);

        Room room = new Room();
        room.setCapacity(3);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setRoomNumber("101");

        Customer customer = new Customer();
        customer.setActive(true);
        customer.setName("Suraj");

        Booking savedBooking = new Booking();
        savedBooking.setCustomer(customer);
        savedBooking.setRoom(room);
        savedBooking.setCheckInDate(request.getCheckIn());
        savedBooking.setCheckOutDate(request.getCheckOut());
        savedBooking.setNumberOfGuests(2);
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(bookingRepo.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepo.save(any())).thenReturn(savedBooking);

        assertNotNull(bookingService.createBooking(request));

        verify(bookingRepo).save(any());
    }

    //  2. Guests <= 0
    @Test
    void shouldThrowExceptionWhenGuestsZero() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(0);

        Room room = new Room();
        room.setCapacity(2);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }

    // 3. Guests exceed capacity
    @Test
    void shouldThrowExceptionWhenGuestsExceedCapacity() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(5);

        Room room = new Room();
        room.setCapacity(2);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }

    //  4. Invalid date range
    @Test
    void shouldThrowExceptionWhenInvalidDateRange() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(1);

        Room room = new Room();
        room.setCapacity(2);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }

    //  5. Check-in in past
    @Test
    void shouldThrowExceptionWhenCheckInInPast() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().minusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(1);

        Room room = new Room();
        room.setCapacity(2);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }

    // 6. Room under maintenance
    @Test
    void shouldThrowExceptionWhenRoomUnderMaintenance() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(1);

        Room room = new Room();
        room.setCapacity(2);
        room.setStatus(RoomStatus.MAINTENANCE);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }

    // 7. Room not found
    @Test
    void shouldThrowExceptionWhenRoomNotAvailable() {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(1);

        Room room = new Room();
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);

        Customer customer = new Customer();
        customer.setActive(true);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(bookingRepo.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Booking()));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(request);
        });
    }
}