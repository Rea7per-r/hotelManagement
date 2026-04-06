package com.suraj.hotelManagement;
import ch.qos.logback.core.encoder.EchoEncoder;
import com.suraj.hotelManagement.model.User;
import com.suraj.hotelManagement.repository.UserRepository;
import com.suraj.hotelManagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserSuccessfully() {


        String username = "test@gmail.com";
        String password = "123";

        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUsername(username);
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any())).thenReturn(savedUser);

        String result = userService.registerCustomer(username, password);

        assertEquals("User registered successfully", result);

    }

}
