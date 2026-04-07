package com.suraj.hotelManagement.kafka;

import com.suraj.hotelManagement.event.PaymentEvent;
import com.suraj.hotelManagement.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(BookingProducer.class);


    public PaymentProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentEvent(PaymentEvent event) {


        kafkaTemplate.send("payment-topic", event)
                .whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send payment event", ex);
                throw new BadRequestException("Kafka producer failed to send");
            } else {
                log.info("Payment Event sent to Kafka topic successfully");
            }
        });
    }
}