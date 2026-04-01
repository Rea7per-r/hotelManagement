package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.model.enums.PaymentStatus;
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
    public void createBooking(Long customerId, Long roomId, LocalDate checkIn, LocalDate checkOut, int guests) {
        Room room= roomRepo.findById(roomId).orElseThrow();
        Customer customer=customerRepo.findById(customerId).orElseThrow();

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        //double total = days * room.getPricePerNight();

        List<Booking> overlaps=bookingRepo.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(room, checkIn, checkOut);

        if(!overlaps.isEmpty())
        {
            throw new RuntimeException("Room is not available");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .room(room)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                //.totalAmount(total)
                .numberOfGuests(guests)
                .status(BookingStatus.CONFIRMED)
                .build();

       bookingRepo.save(booking);


    }

    public List<Booking> getAll() {

        return bookingRepo.findAll();

    }

    public void checkIn(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId).orElseThrow();

        booking.setStatus(BookingStatus.CHECKED_IN);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.BOOKED);

        bookingRepo.save(booking);
    }

    public void checkOut(Long bookingId)
    {
         Booking booking= bookingRepo.findById(bookingId).orElseThrow();

         paymentService.generateBill(booking);
         booking.setStatus(BookingStatus.CHECKED_OUT);

         Room room=booking.getRoom();
         room.setStatus(RoomStatus.AVAILABLE);

         bookingRepo.save(booking);
    }

}
