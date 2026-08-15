package com.thewildchild.management.user.dto.request;

import com.thewildchild.management.role.Role;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100)
    private String firstName;

    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotNull(message = "Role cannot be null")
    private Role role;

}