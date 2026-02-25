package com.aquaculture.service.impl;

import com.aquaculture.dto.request.LoginRequest;
import com.aquaculture.dto.response.LoginResponse;
import com.aquaculture.entity.User;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.UserMapper;
import com.aquaculture.service.AuthService;
import com.aquaculture.util.JwtUtil;
import com.aquaculture.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WxApiUtil wxApiUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 通过code获取openid
        WxApiUtil.WxSession session = wxApiUtil.code2Session(request.getCode());
        if (session == null || session.getOpenid() == null) {
            throw new BusinessException(400, "微信登录失败，请重试");
        }

        String openid = session.getOpenid();
        
        // 查询用户是否存在
        User user = userMapper.findByOpenid(openid);
        
        if (user == null) {
            // 新用户，创建账号
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(session.getUnionid());
            user.setNickname(request.getNickName());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setRole("farmer");
            user.setStatus(1);
            userMapper.insert(user);
            log.info("新用户注册: openid={}", openid);
        } else {
            // 老用户，更新信息
            if (request.getNickName() != null) {
                user.setNickname(request.getNickName());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            userMapper.update(user);
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), openid);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(user);
        
        return response;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public User updateUser(User user) {
        userMapper.update(user);
        return userMapper.findById(user.getId());
    }
}
