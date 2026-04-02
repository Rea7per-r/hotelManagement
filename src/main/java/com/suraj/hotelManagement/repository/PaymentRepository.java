package com.suraj.hotelManagement.repository;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByBooking(Booking booking);
}