package com.suraj.hotelManagement.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private  RoomRepository roomRepo;
    @Autowired
    private  CustomerRepository customerRepo;

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
        this.paymentService=paymentService;
    }


    public Booking createBooking(BookingRequestDTO request) {


        Long customerId = request.getCustomerId();
        Long roomId = request.getRoomId();
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        int guests = request.getGuests();

        Room room= roomRepo.findById(roomId).orElseThrow();
        Customer customer=customerRepo.findById(customerId).orElseThrow();
        // cant have 0 guests
        if (guests <= 0) {
            throw new BadRequestException("Guests must be at least 1");
        }

        //to ensure not to exceed room's capacity
        if (guests > room.getCapacity()) {
            throw new BadRequestException("Guests exceed room capacity");
        }

        //to ensure checkin and checkout dates are in order
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new BadRequestException("Invalid date range");
        }

        //to ensure check-in date isnt in the past
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in cannot be in the past");
        }

        if (!customer.isActive()) {
            throw new BadRequestException("Customer is inactive");
        }


        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new BadRequestException("Room under maintenance");
        }




        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        List<Booking> overlaps=bookingRepo.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(room, checkIn, checkOut);

        if(!overlaps.isEmpty())
        {
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

        return bookingRepo.save(booking);


    }

    public List<Booking> getAll() {

        return bookingRepo.findAll();

    }

    public void checkIn(Long bookingId) {


        Booking booking = bookingRepo.findById(bookingId).orElseThrow();
        //no double check in
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Already checked in");
        }


        booking.setStatus(BookingStatus.CHECKED_IN);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.BOOKED);

        bookingRepo.save(booking);
    }

    public void checkOut(Long bookingId)
    {
         Booking booking= bookingRepo.findById(bookingId).orElseThrow();

        //to ensure no checkout happens without checking in
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Check-in required first");
        }

        // prevention of double checkouts
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("Already checked out");
        }


         paymentService.generateBill(booking);

         booking.setStatus(BookingStatus.CHECKED_OUT);

         Room room=booking.getRoom();
         room.setStatus(RoomStatus.AVAILABLE);

         bookingRepo.save(booking);
    }


    public Booking cancelBooking(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId).orElseThrow();

        // cancelled already
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking already cancelled");
        }

        // checkedin already
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Cannot cancel after check-in");
        }

        //checkedout already
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("Cannot cancel after check-out");
        }


        booking.setStatus(BookingStatus.CANCELLED);

        return bookingRepo.save(booking);
    }


}
