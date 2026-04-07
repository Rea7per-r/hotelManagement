package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.PaymentRequestDTO;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/paymentGateway")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','CUSTOMER')")
    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequestDTO request) {

        log.info("Payment request received | paymentId={} | method={}",
                request.getInvoiceId(), request.getPaymentMethod());

        try {
            paymentService.completePayment(
                    request.getInvoiceId(),
                    request.getPaymentMethod()
            );

            log.info("Payment completed successfully | paymentId={}", request.getInvoiceId());

            return "payment successfully completed";

        } catch (Exception e) {
            log.error("Payment failed | paymentId={} | error={}",
                    request.getInvoiceId(), e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allPayments")
    public List<Payment> getall(Authentication auth) {

        log.info("Fetching all payments (ADMIN access)");

        List<Payment> payments = paymentService.getAll();

        log.info("Total payments fetched = {}", payments.size());

        return payments;
    }

    @PostMapping("/myPayments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Payment> getMyPayments(Authentication auth) {

        String username = auth.getName();

        log.info("Fetching payments for user={}", username);

        List<Payment> payments = paymentService.getMyPayments(username);

        log.info("User {} has {} payments", username, payments.size());

        return payments;
    }
}