package com.thewildchild.management.user.service.validator;

import com.thewildchild.management.common.exception.ValidationException;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;


    // CREATE USER TESTS

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                userValidator.validateCreateUserRequest(request)
        )
                .isInstanceOf(ValidationException.class)
                .hasMessage("User already exists with email : john@example.com");

        // Verify
        verify(userRepository)
                .existsByEmail("john@example.com");

        verify(userRepository, never())
                .existsByPhoneNumber(anyString());
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberAlreadyExists() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543210");

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                userValidator.validateCreateUserRequest(request)
        )
                .isInstanceOf(ValidationException.class)
                .hasMessage(
                        "User already exists with phone number : 9876543210"
                );

        // Verify
        verify(userRepository)
                .existsByEmail("john@example.com");

        verify(userRepository)
                .existsByPhoneNumber("9876543210");
    }

    @Test
    void shouldAllowCreateWhenEmailAndPhoneAreUnique() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543210");

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(false);

        // Act
        userValidator.validateCreateUserRequest(request);

        // Verify
        verify(userRepository)
                .existsByEmail("john@example.com");

        verify(userRepository)
                .existsByPhoneNumber("9876543210");
    }


    // UPDATE USER TESTS

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingPhoneNumber() {

        // Arrange
        User user = new User();
        user.setPhoneNumber("1111111111");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("9876543210");

        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
                userValidator.validateUpdateUserRequest(request, user)
        )
                .isInstanceOf(ValidationException.class)
                .hasMessage(
                        "User already exists with phone number : 9876543210"
                );

        // Verify
        verify(userRepository)
                .existsByPhoneNumber("9876543210");
    }

    @Test
    void shouldAllowUpdateWhenKeepingSamePhoneNumber() {

        // Arrange
        User user = new User();
        user.setPhoneNumber("9876543210");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("9876543210");

        // Act
        userValidator.validateUpdateUserRequest(request, user);

        // Verify
        verify(userRepository, never())
                .existsByPhoneNumber(anyString());
    }

    @Test
    void shouldAllowUpdateWhenChangingToUniquePhoneNumber() {

        // Arrange
        User user = new User();
        user.setPhoneNumber("1111111111");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("9876543210");

        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(false);

        // Act
        userValidator.validateUpdateUserRequest(request, user);

        // Verify
        verify(userRepository)
                .existsByPhoneNumber("9876543210");
    }
}
