package com.suraj.hotelManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.hotelManagement.dto.PaymentRequestDTO;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.enums.PaymentMethod;
import com.suraj.hotelManagement.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)  
class PaymentControllerTest {

    @MockBean
    private com.suraj.hotelManagement.security.JwtFilter jwtFilter;

    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    // TEST 1: Successful payment
    @Test
    void shouldCompletePaymentSuccessfully() throws Exception {

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setInvoiceId(1L);
        request.setPaymentMethod(PaymentMethod.UPI);

        doNothing().when(paymentService)
                .completePayment(1L, PaymentMethod.UPI);

        mockMvc.perform(post("/paymentGateway/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("payment successfully completed"));

        verify(paymentService, times(1))
                .completePayment(1L, PaymentMethod.UPI);
    }

    //  TEST 2: Payment failure
    @Test
    void shouldFailPayment() throws Exception {

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setInvoiceId(1L);
        request.setPaymentMethod(PaymentMethod.UPI);

        doThrow(new RuntimeException("Payment failed"))
                .when(paymentService)
                .completePayment(1L, PaymentMethod.UPI);

        mockMvc.perform(post("/paymentGateway/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentService, times(1))
                .completePayment(1L, PaymentMethod.UPI);
    }

    // TEST 3: Get all payments
    @Test
    void shouldGetAllPayments() throws Exception {

        Payment p1 = new Payment();
        Payment p2 = new Payment();

        when(paymentService.getAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/paymentGateway/allPayments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(paymentService, times(1)).getAll();
    }
}
    //  TEST 4: Get my payments  CANT DO CUS AUTH TOKEN REQUIRED
//    @Test
//    void shouldGetMyPayments() throws Exception {
//
//        Payment p1 = new Payment();
//        Payment p2 = new Payment();
//
//        // ⚠auth.getName() will be NULL since no security
//        when(paymentService.getMyPayments(null))
//                .thenReturn(List.of(p1, p2));
//
//        mockMvc.perform(post("/paymentGateway/myPayments"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2));
//
//        verify(paymentService, times(1))
//                .getMyPayments(null);
//    }
//}