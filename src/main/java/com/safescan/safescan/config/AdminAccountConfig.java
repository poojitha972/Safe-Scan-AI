package com.safescan.safescan.config;

import com.safescan.safescan.User;
import com.safescan.safescan.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminAccountConfig {

    @Bean
    public CommandLineRunner createAdminAccount(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${safescan.admin.email:}") String adminEmail,
            @Value("${safescan.admin.password:}") String adminPassword) {

        return args -> {
            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                return;
            }

            String email = adminEmail.trim().toLowerCase();

            if (userRepository.existsByEmail(email)) {
                return;
            }

            User admin = new User("Admin", email,
                    passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");

            userRepository.save(admin);
        };
    }
}
