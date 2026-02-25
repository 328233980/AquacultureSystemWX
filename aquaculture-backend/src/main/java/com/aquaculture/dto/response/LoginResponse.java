package com.aquaculture.dto.response;

import com.aquaculture.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private User userInfo;
}
