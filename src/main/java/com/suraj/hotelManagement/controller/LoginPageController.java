package com.suraj.hotelManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {

    @GetMapping("/loginPage")
    public String loginPage() {
        return "login"; // Thymeleaf template name
    }
}