package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.exception.BadRequestException;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.model.enums.PaymentMethod;
import com.suraj.hotelManagement.model.enums.PaymentStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service

public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private BookingRepository bookingRepo;

    public Payment generateBill(Booking booking) {


        //only one bill
        if (paymentRepo.existsByBooking(booking)) {
            throw new BadRequestException("Bill already generated");
        }

        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(),booking.getCheckOutDate());

        double baseAmount = days * booking.getRoom().getPricePerNight();

        double tax = baseAmount * 0.18; // 18% GST
        double total = baseAmount + tax;

        Payment payment = Payment.builder()
                .booking(booking)
                .totalAmount(total)
                .taxes(tax)
                //.paymentMethod(method)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        //booking.setPaymentStatus(PaymentStatus.PAID);

        return paymentRepo.save(payment);
    }

    public void completePayment(Long paymentId,PaymentMethod paymentMethod) {

        Payment payment = paymentRepo.findById(paymentId).orElseThrow();




        if(payment.getPaymentStatus()==PaymentStatus.PAID)
        {
            throw new BadRequestException("Payment already done");
        }
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(paymentMethod);

        paymentRepo.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepo.findAll();
    }
}
