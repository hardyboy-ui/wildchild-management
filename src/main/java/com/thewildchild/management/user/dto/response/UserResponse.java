package com.thewildchild.management.user.dto.response;

import com.thewildchild.management.role.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Role role;

    private Boolean active;

    private LocalDateTime lastLoginAt;
}