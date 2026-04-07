package com.suraj.hotelManagement.kafka;

import com.suraj.hotelManagement.event.BookingEvent;
import com.suraj.hotelManagement.event.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BookingConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookingConsumer.class);

    // store events (in-memory)
    private final List<BookingEvent> receivedEvents = new CopyOnWriteArrayList<>();//thread safe

    @KafkaListener(topics = "booking-topic", groupId = "hotel-group")
    public void consume(BookingEvent event) {
        log.info("Processing booking event for notification");

        receivedEvents.add(event);  // store event
        sendEmail(event);
    }

    public List<BookingEvent> getReceivedEvents() {
        return receivedEvents;
    }

    private void sendEmail(BookingEvent event) {
        log.info("EMAIL SENT → Booking {} confirmed for {}",
                event.getBookingId(),
                event.getUsername());
    }
}