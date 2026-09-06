package com.thewildchild.management.config;

import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OwnerUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.owner.email}")
    private String ownerEmail;

    @Value("${bootstrap.owner.password}")
    private String ownerPassword;

    @Value("${bootstrap.owner.first-name}")
    private String ownerFirstName;

    @Value("${bootstrap.owner.last-name}")
    private String ownerLastName;

    @Value("${bootstrap.owner.phone-number}")
    private String ownerPhoneNumber;

    @Override
    @Transactional
    public void run(org.springframework.boot.ApplicationArguments args) {


        if (userRepository.findByEmail(ownerEmail).isPresent()) {
            return;
        }

        User owner = new User();

        owner.setFirstName(ownerFirstName);
        owner.setLastName(ownerLastName);
        owner.setEmail(ownerEmail);
        owner.setPhoneNumber(ownerPhoneNumber);

        owner.setPasswordHash(
                passwordEncoder.encode(ownerPassword)
        );

        owner.setRole(Role.OWNER);
        owner.setActive(true);

        userRepository.save(owner);
    }
}