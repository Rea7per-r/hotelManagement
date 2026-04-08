package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.AuthorizeRequestDTO;
import com.suraj.hotelManagement.dto.RegisterRequestDTO;
import com.suraj.hotelManagement.dto.TokenRequestDTO;
import com.suraj.hotelManagement.model.enums.Role;
import com.suraj.hotelManagement.security.JwtUtil;
import com.suraj.hotelManagement.security.PkceStore;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller   //im returning web pages not just data or json hence i cant use restcontrolller
@RequestMapping("/oauth")
public class OAuthController {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);


    @GetMapping("/home")
    public String homePage(Model model, OAuth2AuthenticationToken auth) {
        String email = auth.getPrincipal().getAttribute("email");
        String name = auth.getPrincipal().getAttribute("name");

        // Generate JWT token
        String token = jwtUtil.generateToken(email, "CUSTOMER");

        model.addAttribute("username", name);
        model.addAttribute("email", email);
        model.addAttribute("jwtToken", token);

        userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setPassword(passwordEncoder.encode("randompassword"));
                    newUser.setRole(Role.CUSTOMER);
                    return userRepository.save(newUser);
                });

        return "home";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }
}
