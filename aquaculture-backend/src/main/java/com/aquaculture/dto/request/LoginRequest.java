package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "code不能为空")
    private String code;
    private String nickName;
    private String avatarUrl;
}
