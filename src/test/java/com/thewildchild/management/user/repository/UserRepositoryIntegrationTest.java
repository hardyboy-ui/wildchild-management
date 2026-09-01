package com.thewildchild.management.user.repository;

import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {

        User user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("9876543210");
        user.setPasswordHash("hashed-password");
        user.setRole(Role.CASHIER);
        user.setActive(true);

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();

        User foundUser =
                userRepository.findById(savedUser.getId())
                        .orElseThrow();

        assertThat(foundUser.getFirstName())
                .isEqualTo("Test");

        assertThat(foundUser.getEmail())
                .isEqualTo("test@example.com");

        assertThat(foundUser.getRole())
                .isEqualTo(Role.CASHIER);

        assertThat(foundUser.getActive())
                .isTrue();
    }
}