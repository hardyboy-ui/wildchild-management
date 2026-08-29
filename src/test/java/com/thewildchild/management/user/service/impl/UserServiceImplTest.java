package com.thewildchild.management.user.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.common.exception.ValidationException;
import com.thewildchild.management.role.Role;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.dto.response.UserResponse;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import com.thewildchild.management.user.service.mapper.UserMapper;
import com.thewildchild.management.user.service.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("password123");
        request.setRole(Role.CASHIER);

        User user = new User();
        user.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("john@example.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(savedUser.getId());
        expectedResponse.setEmail(savedUser.getEmail());

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(user))
                .thenReturn(savedUser);

        when(userMapper.toResponse(savedUser))
                .thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse =
                userService.createUser(request);

        // Assert
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);

        verify(userValidator)
                .validateCreateUserRequest(request);

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(user);

        assertThat(user.getPasswordHash())
                .isEqualTo("hashedPassword");
    }


    @Test
    void shouldRejectUserWhenEmailAlreadyExists() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("password123");
        request.setRole(Role.CASHIER);

        doThrow(new ValidationException(
                "User already exists with email : john@example.com"
        ))
                .when(userValidator)
                .validateCreateUserRequest(request);

        // Act & Assert
        assertThatThrownBy(() ->
                userService.createUser(request)
        )
                .isInstanceOf(ValidationException.class)
                .hasMessage(
                        "User already exists with email : john@example.com"
                );

        // Verify that processing stopped after validation
        verify(userValidator)
                .validateCreateUserRequest(request);

        verifyNoInteractions(
                userMapper,
                passwordEncoder,
                userRepository
        );
    }

    @Test
    void shouldGetUserByIdSuccessfully() {

        // Arrange
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setEmail("john@example.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse =
                userService.getUserById(userId);

        // Assert
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);

        // Verify
        verify(userRepository)
                .findById(userId);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                userService.getUserById(userId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id : " + userId);

        // Verify
        verify(userRepository)
                .findById(userId);

        verifyNoInteractions(userMapper);
    }


    @Test
    void shouldGetAllUsersSuccessfully() {

        // Arrange
        User user1 = new User();
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setEmail("jane@example.com");

        UserResponse response1 = new UserResponse();
        response1.setEmail("john@example.com");

        UserResponse response2 = new UserResponse();
        response2.setEmail("jane@example.com");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        when(userMapper.toResponse(user1))
                .thenReturn(response1);

        when(userMapper.toResponse(user2))
                .thenReturn(response2);

        // Act
        List<UserResponse> actualResponse =
                userService.getAllUsers();

        // Assert
        assertThat(actualResponse)
                .containsExactly(response1, response2);

        // Verify
        verify(userRepository)
                .findAll();

        verify(userMapper)
                .toResponse(user1);

        verify(userMapper)
                .toResponse(user2);
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        // Arrange
        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPhoneNumber("9876543210");
        request.setActive(true);

        User user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setFirstName("Updated");
        expectedResponse.setLastName("User");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse =
                userService.updateUser(userId, request);

        // Assert
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);

        // Verify
        verify(userRepository)
                .findById(userId);

        verify(userValidator)
                .validateUpdateUserRequest(request, user);

        verify(userMapper)
                .updateEntity(user, request);

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {

        // Arrange
        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                userService.updateUser(userId, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id : " + userId);

        // Verify
        verify(userRepository)
                .findById(userId);

        verifyNoInteractions(
                userValidator,
                userMapper
        );

        verify(userRepository, never())
                .save(any(User.class));
    }


    @Test
    void shouldRejectUpdateWhenPhoneNumberAlreadyExists() {

        // Arrange
        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("9876543210");

        User user = new User();
        user.setId(userId);
        user.setPhoneNumber("9999999999");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        doThrow(new ValidationException(
                "User already exists with phone number : 9876543210"
        ))
                .when(userValidator)
                .validateUpdateUserRequest(request, user);

        // Act & Assert
        assertThatThrownBy(() ->
                userService.updateUser(userId, request)
        )
                .isInstanceOf(ValidationException.class)
                .hasMessage(
                        "User already exists with phone number : 9876543210"
                );

        // Verify
        verify(userRepository)
                .findById(userId);

        verify(userValidator)
                .validateUpdateUserRequest(request, user);

        verifyNoInteractions(userMapper);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {

        // Arrange
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                userService.deleteUser(userId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id : " + userId);

        // Verify
        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .delete(any(User.class));
    }

}

