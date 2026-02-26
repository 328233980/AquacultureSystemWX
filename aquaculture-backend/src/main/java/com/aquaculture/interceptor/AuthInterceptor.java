package com.aquaculture.interceptor;

import com.aquaculture.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${test.mode:false}")
    private boolean testMode;

    @Value("${test.default-token:888888}")
    private String defaultToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        // 测试模式下，如果token为空则使用默认token
        if (testMode && (token == null || token.isEmpty())) {
            log.info("测试模式：使用默认token");
            token = defaultToken;
        }
        
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return false;
        }

        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 测试模式下，如果token等于默认token，则直接设置测试用户ID
        if (testMode && token.equals(defaultToken)) {
            log.info("测试模式：使用测试用户ID 1");
            request.setAttribute("userId", 1L);
            request.setAttribute("openid", "test_openid");
            return true;
        }

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return false;
        }

        // 将用户ID存入request属性中，供后续使用
        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("userId", userId);
        request.setAttribute("openid", jwtUtil.getOpenid(token));

        return true;
    }
}
