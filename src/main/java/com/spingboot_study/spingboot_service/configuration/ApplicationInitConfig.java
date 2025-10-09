package com.spingboot_study.spingboot_service.configuration;

import com.spingboot_study.spingboot_service.entity.Role;
import com.spingboot_study.spingboot_service.entity.User;
import com.spingboot_study.spingboot_service.repository.PermissionRepository;
import com.spingboot_study.spingboot_service.repository.RoleRepository;
import com.spingboot_study.spingboot_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("USER")
                        .description("User role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .name("ADMIN")
                        .description("Admin role")
                        .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder().
                        username("admin")
                        .password(passwordEncoder.encode("admin")) // In a real application, use a password encoder
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.info("Admin has been created with username: admin and password: admin");
            }
        };
    }
}
