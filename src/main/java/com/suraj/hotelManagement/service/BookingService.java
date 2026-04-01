package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Customer;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private  RoomRepository roomRepo;
    @Autowired
    private  CustomerRepository customerRepo;

    public BookingService(
            BookingRepository bookingRepo,
            RoomRepository roomRepo,
            CustomerRepository customerRepo
    ) {
        this.bookingRepo = bookingRepo;
        this.roomRepo = roomRepo;
        this.customerRepo = customerRepo;
    }
    public void createBooking(Long customerId, Long roomId, LocalDate checkIn, LocalDate checkOut, int guests) {
        Room room= roomRepo.findById(roomId).orElseThrow();
        Customer customer=customerRepo.findById(customerId).orElseThrow();

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
                .numberOfGuests(guests)
                .status(BookingStatus.CONFIRMED)
                .build();

        bookingRepo.save(booking);

    }

    public List<Booking> getAll() {

        return bookingRepo.findAll();

    }
}
