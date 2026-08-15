package com.thewildchild.management.user.service.validator;

import com.thewildchild.management.common.exception.ValidationException;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateCreateUserRequest(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException(
                    "User already exists with email : "
                            + request.getEmail());
        }

        if (request.getPhoneNumber() != null &&
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {

            throw new ValidationException(
                    "User already exists with phone number : "
                            + request.getPhoneNumber());
        }
    }

    public void validateUpdateUserRequest(UpdateUserRequest request,User user) {

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {

            throw new ValidationException(
                    "User already exists with phone number : "
                            + request.getPhoneNumber());
        }
    }
}