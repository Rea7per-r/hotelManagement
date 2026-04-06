    package com.suraj.hotelManagement.service;

    import com.suraj.hotelManagement.model.User;
    import com.suraj.hotelManagement.model.enums.Role;
    import com.suraj.hotelManagement.repository.UserRepository;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    @Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public String registerCustomer(String username, String password) {

        log.info("Register request received | username={}", username);

        // Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            log.error("User already exists | username={}", username);
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));

        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        log.info("User registered successfully | username={}", username);

        return "User registered successfully";
    }
}