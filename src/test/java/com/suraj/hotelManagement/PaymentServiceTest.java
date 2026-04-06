package com.suraj.hotelManagement;

import com.suraj.hotelManagement.exception.BadRequestException;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.PaymentMethod;
import com.suraj.hotelManagement.model.enums.PaymentStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.repository.PaymentRepository;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepo;



    @InjectMocks
    private PaymentService paymentService;

    // 1. Generate bill successfully
    @Test
    void shouldGenerateBillSuccessfully() {

        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(2));

        Room room = new Room();
        room.setPricePerNight(1000);
        booking.setRoom(room);

        when(paymentRepo.existsByBooking(booking)).thenReturn(false);
        when(paymentRepo.save(any())).thenReturn(new Payment());

        Payment result = paymentService.generateBill(booking);

        assertNotNull(result);
        verify(paymentRepo).save(any());
    }

    //  2. Bill already exists
    @Test
    void shouldThrowExceptionWhenBillAlreadyExists() {

        Booking booking = new Booking();

        when(paymentRepo.existsByBooking(booking)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            paymentService.generateBill(booking);
        });
    }

    //  3. Complete payment successfully
    @Test
    void shouldCompletePaymentSuccessfully() {

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.completePayment(1L, PaymentMethod.CARD);

        assertEquals(PaymentStatus.PAID, payment.getPaymentStatus());
        verify(paymentRepo).save(payment);
    }

    //  4. Payment already done
    @Test
    void shouldThrowExceptionWhenPaymentAlreadyDone() {

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PAID);

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(BadRequestException.class, () -> {
            paymentService.completePayment(1L, PaymentMethod.CARD);
        });
    }

    //  5. Get all payments
    @Test
    void shouldReturnAllPayments() {

        when(paymentRepo.findAll()).thenReturn(Collections.singletonList(new Payment()));

        assertEquals(1, paymentService.getAll().size());
    }

    //  6. Get payments by username
    @Test
    void shouldReturnPaymentsByUsername() {

        when(paymentRepo.findPaymentsByCustomerEmail("test@gmail.com"))
                .thenReturn(Collections.singletonList(new Payment()));

        assertEquals(1, paymentService.getMyPayments("test@gmail.com").size());
    }
}