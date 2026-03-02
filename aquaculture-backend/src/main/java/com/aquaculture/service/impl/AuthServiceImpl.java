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
        log.info("登录请求: code={}, nickName={}, avatarUrl={}", 
                request.getCode(), request.getNickName(), request.getAvatarUrl());
        
        // 通过code获取openid
        WxApiUtil.WxSession session = wxApiUtil.code2Session(request.getCode());
        if (session == null || session.getOpenid() == null) {
            log.error("微信登录失败: session={}", session);
            throw new BusinessException(400, "微信登录失败，请重试");
        }

        String openid = session.getOpenid();
        
        // 查询用户是否存在
        User user = userMapper.findByOpenid(openid);
        
        if (user == null) {
            // 新用户，创建账号
            log.info("新用户注册: openid={}", openid);
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(session.getUnionid());
            user.setNickname(request.getNickName());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setRole("farmer");
            user.setStatus(1);
            try {
                userMapper.insert(user);
                log.info("用户创建成功: userId={}", user.getId());
            } catch (Exception e) {
                log.error("用户创建失败: openid={}, error={}", openid, e.getMessage(), e);
                throw new BusinessException(500, "用户创建失败: " + e.getMessage());
            }
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
        String token;
        try {
            token = jwtUtil.generateToken(user.getId(), openid);
        } catch (Exception e) {
            log.error("Token生成失败: userId={}, error={}", user.getId(), e.getMessage(), e);
            throw new BusinessException(500, "Token生成失败");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(user);
        
        log.info("登录成功: userId={}, openid={}", user.getId(), openid);
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
