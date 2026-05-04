package com.smartsure.auth.config;

import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@smartsure.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@smartsure.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .phone("9999999999")
                    .address("Office HQ")
                    .role(Role.ADMIN)
                    .status("ACTIVE")
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("customer@example.com").isEmpty()) {
            User customer = User.builder()
                    .name("Test Customer")
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .phone("8888888888")
                    .address("Customer Address")
                    .role(Role.CUSTOMER)
                    .status("ACTIVE")
                    .build();
            userRepository.save(customer);
        }
    }
}
