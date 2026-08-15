package com.thewildchild.management.user.service.impl;

import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.dto.response.UserResponse;
import com.thewildchild.management.user.entity.User;
import com.thewildchild.management.user.repository.UserRepository;
import com.thewildchild.management.user.service.UserService;
import com.thewildchild.management.user.service.mapper.UserMapper;
import com.thewildchild.management.user.service.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserValidator userValidator;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        userValidator.validateCreateUserRequest(request);

        User user = userMapper.toEntity(request);

        String passwordHash = passwordEncoder.encode(request.getPassword());   // Addition of Bcrypt hashing
        user.setPasswordHash(passwordHash);
 
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {

        User user = findUserById(userId);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID userId,
                                   UpdateUserRequest request) {

        User user = findUserById(userId);

        userValidator.validateUpdateUserRequest(request, user);

        userMapper.updateEntity(user, request);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(UUID userId) {

        User user = findUserById(userId);

        userRepository.delete(user);
    }

    private User findUserById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + userId));
    }
}