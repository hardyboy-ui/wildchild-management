package com.thewildchild.management.user.service;

import com.thewildchild.management.user.dto.request.CreateUserRequest;
import com.thewildchild.management.user.dto.request.UpdateUserRequest;
import com.thewildchild.management.user.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    void deleteUser(UUID userId);
    
}