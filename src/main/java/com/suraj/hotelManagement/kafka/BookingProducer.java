package com.suraj.hotelManagement.kafka;

import com.suraj.hotelManagement.event.BookingEvent;
import com.suraj.hotelManagement.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {
    private static final Logger log = LoggerFactory.getLogger(BookingProducer.class);
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingProducer(KafkaTemplate<String, BookingEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingEvent(BookingEvent event) {
        kafkaTemplate.send("booking-topic", event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send booking event", ex);
                        throw new BadRequestException("Kafka producer failed to send");
                    } else {
                        log.info("Booking Event sent to Kafka topic successfully");
                    }
                });
    }


}