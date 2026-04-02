package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
    }
}