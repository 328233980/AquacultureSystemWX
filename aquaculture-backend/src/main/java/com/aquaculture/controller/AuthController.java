package com.aquaculture.controller;

import com.aquaculture.dto.request.LoginRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.dto.response.LoginResponse;
import com.aquaculture.entity.User;
import com.aquaculture.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/user")
    public ApiResponse<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = authService.getUserById(userId);
        return ApiResponse.success(user);
    }

    @PutMapping("/user")
    public ApiResponse<User> updateUser(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        User updatedUser = authService.updateUser(user);
        return ApiResponse.success(updatedUser);
    }
}
