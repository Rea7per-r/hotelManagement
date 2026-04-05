package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.exception.BadRequestException;
import com.suraj.hotelManagement.model.Booking;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.enums.PaymentMethod;
import com.suraj.hotelManagement.model.enums.PaymentStatus;
import com.suraj.hotelManagement.repository.BookingRepository;
import com.suraj.hotelManagement.repository.CustomerRepository;
import com.suraj.hotelManagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private CustomerRepository customerRepository;

    public Payment generateBill(Booking booking) {

        log.info("Generating bill for bookingId={}", booking.getBookingId());

        if (paymentRepo.existsByBooking(booking)) {
            log.warn("Bill already exists for bookingId={}", booking.getBookingId());
            throw new BadRequestException("Bill already generated");
        }

        long days = ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        double baseAmount = days * booking.getRoom().getPricePerNight();
        double tax = baseAmount * 0.18;
        double total = baseAmount + tax;

        log.info("Bill calculated | bookingId={} | days={} | base={} | tax={} | total={}",
                booking.getBookingId(), days, baseAmount, tax, total);

        Payment payment = Payment.builder()
                .booking(booking)
                .totalAmount(total)
                .taxes(tax)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepo.save(payment);

        log.info("Bill generated successfully | paymentId={} | bookingId={}",
                savedPayment.getInvoiceId(), booking.getBookingId());

        return savedPayment;
    }

    public void completePayment(Long paymentId, PaymentMethod paymentMethod) {

        log.info("Processing payment | paymentId={} | method={}", paymentId, paymentMethod);

        Payment payment = paymentRepo.findById(paymentId).orElseThrow();

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            log.warn("Payment already completed | paymentId={}", paymentId);
            throw new BadRequestException("Payment already done");
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(paymentMethod);

        paymentRepo.save(payment);

        log.info("Payment marked as PAID | paymentId={}", paymentId);
    }

    public List<Payment> getAll() {

        log.info("Fetching all payments from database");

        List<Payment> payments = paymentRepo.findAll();

        log.info("Total payments fetched = {}", payments.size());

        return payments;
    }

    public List<Payment> getMyPayments(String username) {

        log.info("Fetching payments for customer email={}", username);

        List<Payment> payments = paymentRepo.findPaymentsByCustomerEmail(username);

        log.info("Payments fetched for {} = {}", username, payments.size());

        return payments;
    }
}