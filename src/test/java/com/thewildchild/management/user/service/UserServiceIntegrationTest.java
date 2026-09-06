package com.thewildchild.management.user.service;

import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.dto.response.UserResponse;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void shouldCreateUser() {

        CreateUserRequest request = new CreateUserRequest();

        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("password123");
        request.setRole(Role.CASHIER);

        UserResponse response =
                userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getFirstName())
                .isEqualTo("Test");
        assertThat(response.getEmail())
                .isEqualTo("test@example.com");
        assertThat(response.getRole())
                .isEqualTo(Role.CASHIER);

        User savedUser =
                userRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(savedUser.getPasswordHash())
                .isNotEqualTo("password123");

        assertThat(savedUser.getPasswordHash())
                .isNotBlank();
    }


    @Test
    void shouldGetUserById() {

        CreateUserRequest request = new CreateUserRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543211");
        request.setPassword("password123");
        request.setRole(Role.WAITER);

        UserResponse createdUser =
                userService.createUser(request);

        UserResponse response =
                userService.getUserById(
                        createdUser.getId()
                );

        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo(createdUser.getId());
        assertThat(response.getFirstName())
                .isEqualTo("John");
        assertThat(response.getEmail())
                .isEqualTo("john@example.com");
        assertThat(response.getRole())
                .isEqualTo(Role.WAITER);
    }


    @Test
    void shouldGetAllUsers() {

        CreateUserRequest firstRequest =
                createUserRequest(
                        "First",
                        "User",
                        "first@example.com",
                        "9876543212"
                );

        CreateUserRequest secondRequest =
                createUserRequest(
                        "Second",
                        "User",
                        "second@example.com",
                        "9876543213"
                );

        userService.createUser(firstRequest);
        userService.createUser(secondRequest);

        List<UserResponse> users =
                userService.getAllUsers();

        assertThat(users)
                .hasSize(2);

        assertThat(users)
                .extracting(UserResponse::getEmail)
                .containsExactlyInAnyOrder(
                        "first@example.com",
                        "second@example.com"
                );
    }


    @Test
    void shouldUpdateUser() {

        CreateUserRequest createRequest =
                createUserRequest(
                        "Old",
                        "Name",
                        "old@example.com",
                        "9876543214"
                );

        UserResponse createdUser =
                userService.createUser(createRequest);

        UpdateUserRequest updateRequest =
                new UpdateUserRequest();

        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setPhoneNumber("9999999999");

        UserResponse updatedUser =
                userService.updateUser(
                        createdUser.getId(),
                        updateRequest
                );

        assertThat(updatedUser.getId())
                .isEqualTo(createdUser.getId());

        assertThat(updatedUser.getFirstName())
                .isEqualTo("Updated");

        assertThat(updatedUser.getLastName())
                .isEqualTo("User");

        assertThat(updatedUser.getPhoneNumber())
                .isEqualTo("9999999999");

        User savedUser =
                userRepository.findById(createdUser.getId())
                        .orElseThrow();

        assertThat(savedUser.getFirstName())
                .isEqualTo("Updated");

        assertThat(savedUser.getPhoneNumber())
                .isEqualTo("9999999999");
    }


    @Test
    void shouldDeleteUser() {

        CreateUserRequest request =
                createUserRequest(
                        "Delete",
                        "User",
                        "delete@example.com",
                        "9876543215"
                );

        UserResponse createdUser =
                userService.createUser(request);

        UUID userId = createdUser.getId();

        assertThat(userRepository.existsById(userId))
                .isTrue();

        userService.deleteUser(userId);

        assertThat(userRepository.existsById(userId))
                .isFalse();
    }


    private CreateUserRequest createUserRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {

        CreateUserRequest request =
                new CreateUserRequest();

        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);
        request.setPassword("password123");
        request.setRole(Role.CASHIER);

        return request;
    }
}