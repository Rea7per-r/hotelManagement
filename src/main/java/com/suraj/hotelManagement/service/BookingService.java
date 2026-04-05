package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.dto.BookingRequestDTO;
import com.suraj.hotelManagement.dto.BookingResponseDTO;
import com.suraj.hotelManagement.exception.BadRequestException;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.model.enums.RoomStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PaymentService paymentService;

    public BookingService(
            BookingRepository bookingRepo,
            RoomRepository roomRepo,
            CustomerRepository customerRepo, PaymentService paymentService
    ) {
        this.bookingRepo = bookingRepo;
        this.roomRepo = roomRepo;
        this.customerRepo = customerRepo;
        this.paymentService = paymentService;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO request) {

        Long customerId = request.getCustomerId();
        Long roomId = request.getRoomId();
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        int guests = request.getGuests();

        log.info("Creating booking request | customerId={} | roomId={} | checkIn={} | checkOut={} | guests={}",
                customerId, roomId, checkIn, checkOut, guests);

        Room room = roomRepo.findById(roomId).orElseThrow(() -> {
            log.error("Room not found | roomId={}", roomId);
            return new RuntimeException();
        });

        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> {
            log.error("Customer not found | customerId={}", customerId);
            return new RuntimeException();
        });

        if (guests <= 0) {
            log.warn("Invalid guests count | guests={}", guests);
            throw new BadRequestException("Guests must be at least 1");
        }

        if (guests > room.getCapacity()) {
            log.warn("Guests exceed capacity | guests={} | capacity={}", guests, room.getCapacity());
            throw new BadRequestException("Guests exceed room capacity");
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            log.warn("Invalid date range | checkIn={} | checkOut={}", checkIn, checkOut);
            throw new BadRequestException("Invalid date range");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            log.warn("Check-in in past | checkIn={}", checkIn);
            throw new BadRequestException("Check-in cannot be in the past");
        }

        if (!customer.isActive()) {
            log.warn("Inactive customer | customerId={}", customerId);
            throw new BadRequestException("Customer is inactive");
        }

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            log.warn("Room under maintenance | roomId={}", roomId);
            throw new BadRequestException("Room under maintenance");
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        List<Booking> overlaps = bookingRepo
                .findByRoomAndCheckOutDateAfterAndCheckInDateBefore(room, checkIn, checkOut);

        if (!overlaps.isEmpty()) {
            log.warn("Room not available | roomId={} | overlappingBookings={}", roomId, overlaps.size());
            throw new BadRequestException("Room is not available");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .room(room)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(guests)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepo.save(booking);

        log.info("Booking created successfully | bookingId={} | customerId={} | roomId={}",
                saved.getBookingId(), customerId, roomId);

        return mapToResponse(saved);
    }


    public List<Booking> getAll() {

        log.info("Fetching all bookings");

        List<Booking> bookings = bookingRepo.findAll();

        log.info("Total bookings fetched = {}", bookings.size());

        return bookings;
    }

    public void checkIn(Long bookingId) {

        log.info("Check-in requested | bookingId={}", bookingId);

        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found | bookingId={}", bookingId);
            return new RuntimeException();
        });

        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            log.warn("Already checked in | bookingId={}", bookingId);
            throw new BadRequestException("Already checked in");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            log.warn("Already checked out | bookingId={}", bookingId);
            throw new BadRequestException("already checked out");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.BOOKED);

        bookingRepo.save(booking);

        log.info("Check-in successful | bookingId={}", bookingId);
    }

    public void checkOut(Long bookingId) {

        log.info("Check-out requested | bookingId={}", bookingId);

        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found | bookingId={}", bookingId);
            return new RuntimeException();
        });

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            log.warn("Check-in required before checkout | bookingId={}", bookingId);
            throw new BadRequestException("Check-in required first");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            log.warn("Already checked out | bookingId={}", bookingId);
            throw new BadRequestException("Already checked out");
        }

        log.info("Generating bill before checkout | bookingId={}", bookingId);
        paymentService.generateBill(booking);

        booking.setStatus(BookingStatus.CHECKED_OUT);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);

        bookingRepo.save(booking);

        log.info("Check-out completed | bookingId={}", bookingId);
    }


    public BookingResponseDTO cancelBooking(Long bookingId) {

        log.info("Cancel booking requested | bookingId={}", bookingId);

        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found | bookingId={}", bookingId);
            return new RuntimeException();
        });

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Already cancelled | bookingId={}", bookingId);
            throw new BadRequestException("Booking already cancelled");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            log.warn("Cannot cancel after check-in | bookingId={}", bookingId);
            throw new BadRequestException("Cannot cancel after check-in");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            log.warn("Cannot cancel after check-out | bookingId={}", bookingId);
            throw new BadRequestException("Cannot cancel after check-out");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking saved = bookingRepo.save(booking);

        log.info("Booking cancelled successfully | bookingId={}", bookingId);

        return mapToResponse(saved);
    }

    private BookingResponseDTO mapToResponse(Booking booking) {
        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .customerName(booking.getCustomer().getName())
                .roomNumber(booking.getRoom().getRoomNumber())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getNumberOfGuests())
                .status(booking.getStatus().name())
                .build();
    }

    public List<Booking> getByUsername(String username) {

        log.info("Fetching bookings for user={}", username);

        Customer customer = customerRepo
                .findByEmail(username)
                .orElseThrow(() -> {
                    log.error("Customer not found | email={}", username);
                    return new RuntimeException("Customer not found");
                });

        List<Booking> bookings = bookingRepo.findByCustomerCustomerId(customer.getCustomerId());

        log.info("User {} has {} bookings", username, bookings.size());

        return bookings;
    }
}
