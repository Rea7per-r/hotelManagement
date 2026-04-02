package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.PaymentRequestDTO;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.enums.PaymentMethod;
import com.suraj.hotelManagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paymentGateway")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequestDTO request) {

        paymentService.completePayment(
                request.getInvoiceId(),
                request.getPaymentMethod()
        );

        return "payment successfully completed";
    }

    @GetMapping("/allPayments")
    public List<Payment> getall()
    {
        return paymentService.getAll();
    }

}
