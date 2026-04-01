package com.suraj.hotelManagement.repository;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndCheckOutDateAfterAndCheckInDateBefore(Room room, LocalDate checkIn, LocalDate checkOut);
}
