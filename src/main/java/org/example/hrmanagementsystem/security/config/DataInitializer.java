package org.example.hrmanagementsystem.security.config;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.enums.RoleType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0 ) { //it ensures the admin creation logic runs only once on startup
            User admin = User.builder()
                    .username("sadmin")
                    .password(passwordEncoder.encode("sadmin123"))
                    .role(RoleType.ADMIN)
                    .build();
            userRepository.save(admin);
                    System.out.println("Super admin created!");
        }
    }
}
