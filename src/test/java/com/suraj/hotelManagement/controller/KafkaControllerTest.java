package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.controller.KafkaController;
import com.suraj.hotelManagement.event.BookingEvent;
import com.suraj.hotelManagement.event.PaymentEvent;
import com.suraj.hotelManagement.kafka.BookingConsumer;
import com.suraj.hotelManagement.kafka.PaymentConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KafkaController.class)
@AutoConfigureMockMvc(addFilters = false)
class KafkaControllerTest {
    @MockBean
    private com.suraj.hotelManagement.security.JwtUtil jwtUtil;
    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingConsumer bookingConsumer;

    @MockBean
    private PaymentConsumer paymentConsumer;

    //  TEST /kafka/events
    @Test
    void shouldGetAllBookingEvents() throws Exception {

        BookingEvent event = new BookingEvent();
        event.setBookingId(1L);

        when(bookingConsumer.getReceivedEvents())
                .thenReturn(List.of(event));

        mockMvc.perform(get("/kafka/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookingId").value(1));
    }

    //  TEST /kafka/payments
    @Test
    void shouldGetAllPayments() throws Exception {

        PaymentEvent payment = new PaymentEvent();
        payment.setBookingId(10L);

        when(paymentConsumer.getAllPayments())
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/kafka/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookingId").value(10));
    }
}