package com.suraj.hotelManagement.kafka;

import com.suraj.hotelManagement.event.PaymentEvent;
import com.suraj.hotelManagement.event.BookingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private final List<PaymentEvent> receivedPayments = new CopyOnWriteArrayList<>();


    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void consume(PaymentEvent event) {
        log.info("KAFKA PAYMENT EVENT → Booking {} | Status {}",
                event.getBookingId(),
                event.getStatus());

        receivedPayments.add(event);
        log.info("PAYMENT RECEIPT SENT (simulated) → Booking {}", event.getBookingId());
    }
    public List<PaymentEvent> getAllPayments() {
        return receivedPayments;
    }
}