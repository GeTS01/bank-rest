package com.example.bankcards.config;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfMissing(userRepository, passwordEncoder, "admin", "admin12345", "System Admin", Role.ADMIN);
            createUserIfMissing(userRepository, passwordEncoder, "user", "user12345", "Demo User", Role.USER);
        };
    }

    private static void createUserIfMissing(UserRepository userRepository,
                                            PasswordEncoder passwordEncoder,
                                            String username,
                                            String password,
                                            String fullName,
                                            Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        userRepository.save(user);
    }
}
