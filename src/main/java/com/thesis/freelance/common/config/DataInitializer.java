package com.thesis.freelance.common.config;

import com.thesis.freelance.features.user.entity.UserRole;
import com.thesis.freelance.features.user.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataInitializer {

    private final UserRoleRepository userRoleRepository;

    public DataInitializer(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Bean
    public CommandLineRunner initializeRoles() {
        return args -> {
            initializeRole(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Client");
            initializeRole(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Admin");
            initializeRole(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Freelancer");
        };
    }

    private void initializeRole(UUID id, String roleName) {
        if (userRoleRepository.findById(id).isEmpty()) {
            UserRole role = new UserRole();
            role.setId(id);
            role.setRole(roleName);
            userRoleRepository.save(role);
            System.out.println("Initialized role: " + roleName + " with ID: " + id);
        } else {
            System.out.println("Role " + roleName + " already exists, skipping initialization");
        }
    }
}
