package com.suraj.hotelManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin Access Granted";
    }

    @GetMapping("/reception/test")
    public String receptionTest() {
        return "Reception Access Granted";
    }

    @GetMapping("/customer/test")
    public String customerTest() {
        return "Customer Access Granted";
    }
}