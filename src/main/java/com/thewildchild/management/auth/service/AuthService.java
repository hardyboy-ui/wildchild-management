package com.thewildchild.management.auth.service;

import com.thewildchild.management.auth.dto.request.LoginRequest;
import com.thewildchild.management.auth.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}