package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.event.BookingEvent;
import com.suraj.hotelManagement.event.PaymentEvent;
import com.suraj.hotelManagement.kafka.BookingConsumer;
import com.suraj.hotelManagement.kafka.PaymentConsumer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kafka")
@AllArgsConstructor
public class KafkaController {

    @Autowired
    private final BookingConsumer bookingConsumer;
    @Autowired
    private  final PaymentConsumer paymentConsumer;



    @GetMapping("/events")
    public List<BookingEvent> getAllEvents() {

        return bookingConsumer.getReceivedEvents();
    }

    @GetMapping("/payments")
    public List<PaymentEvent> getPayments() {
        return paymentConsumer.getAllPayments();
    }
}