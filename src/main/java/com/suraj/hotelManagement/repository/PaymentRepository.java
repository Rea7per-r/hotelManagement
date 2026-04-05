package com.suraj.hotelManagement.repository;

import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByBooking(Booking booking);



    //List<Payment> findByBookingIn(List<Booking> bookings);

    @Query("SELECT p FROM Payment p WHERE p.booking.customer.email = :email")
    List<Payment> findPaymentsByCustomerEmail(@Param("email") String email);}