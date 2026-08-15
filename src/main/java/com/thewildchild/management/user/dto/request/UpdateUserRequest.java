package com.thewildchild.management.user.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Boolean active;

}