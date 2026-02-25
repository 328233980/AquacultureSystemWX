package com.aquaculture.service;

import com.aquaculture.dto.request.LoginRequest;
import com.aquaculture.dto.response.LoginResponse;
import com.aquaculture.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    User getUserById(Long userId);
    User updateUser(User user);
}
