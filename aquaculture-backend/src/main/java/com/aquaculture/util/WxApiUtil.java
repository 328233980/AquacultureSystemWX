package com.aquaculture.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WxApiUtil {

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String SEND_SUBSCRIBE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
    
    // access_token 缓存
    private volatile String accessToken;
    private volatile long accessTokenExpireTime = 0;
    private final Object tokenLock = new Object();

    /**
     * 通过code获取openid和session_key
     */
    public WxSession code2Session(String code) {
        String url = JSCODE2SESSION_URL + "?appid=" + appid 
                + "&secret=" + secret 
                + "&js_code=" + code 
                + "&grant_type=authorization_code";
        
        log.info("调用微信API: appid={}, code={}", appid, code);
        
        try {
            String response = HttpUtil.get(url, 10000); // 增加超时时间到10秒
            log.info("微信登录响应: {}", response);
            
            JSONObject json = JSONUtil.parseObj(response);
            
            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                int errcode = json.getInt("errcode");
                String errmsg = json.getStr("errmsg");
                log.error("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);
                return null;
            }
            
            WxSession session = new WxSession();
            session.setOpenid(json.getStr("openid"));
            session.setSessionKey(json.getStr("session_key"));
            session.setUnionid(json.getStr("unionid"));
            
            log.info("微信登录成功: openid={}", session.getOpenid());
            return session;
        } catch (Exception e) {
            log.error("调用微信API异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取 access_token（带缓存）
     */
    public String getAccessToken() {
        // 检查缓存是否有效（提前5分钟刷新）
        if (accessToken != null && System.currentTimeMillis() < accessTokenExpireTime - 5 * 60 * 1000) {
            return accessToken;
        }
        
        synchronized (tokenLock) {
            // 双重检查
            if (accessToken != null && System.currentTimeMillis() < accessTokenExpireTime - 5 * 60 * 1000) {
                return accessToken;
            }
            
            String url = ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
            
            try {
                String response = HttpUtil.get(url, 10000);
                log.info("获取access_token响应: {}", response);
                
                JSONObject json = JSONUtil.parseObj(response);
                
                if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                    log.error("获取access_token失败: {}", response);
                    return null;
                }
                
                accessToken = json.getStr("access_token");
                int expiresIn = json.getInt("expires_in", 7200);
                accessTokenExpireTime = System.currentTimeMillis() + expiresIn * 1000;
                
                log.info("获取access_token成功, 有效期: {}秒", expiresIn);
                return accessToken;
            } catch (Exception e) {
                log.error("获取access_token异常: {}", e.getMessage(), e);
                return null;
            }
        }
    }
    
    /**
     * 发送订阅消息
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param data 模板数据 (key为模板中的字段名，value为内容)
     * @param page 跳转页面
     * @return 是否发送成功
     */
    public boolean sendSubscribeMessage(String openid, String templateId, Map<String, String> data, String page) {
        String token = getAccessToken();
        if (token == null) {
            log.error("发送订阅消息失败: 无法获取access_token");
            return false;
        }
        
        String url = SEND_SUBSCRIBE_MSG_URL + "?access_token=" + token;
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("touser", openid);
        requestBody.set("template_id", templateId);
        if (page != null && !page.isEmpty()) {
            requestBody.set("page", page);
        }
        
        // 构建data部分
        JSONObject dataObj = new JSONObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JSONObject valueObj = new JSONObject();
            valueObj.set("value", entry.getValue());
            dataObj.set(entry.getKey(), valueObj);
        }
        requestBody.set("data", dataObj);
        
        // 设置跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
        requestBody.set("miniprogram_state", "formal");
        // 进入小程序查看”的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN
        requestBody.set("lang", "zh_CN");
        
        try {
            String response = HttpUtil.post(url, requestBody.toString(), 10000);
            log.info("发送订阅消息响应: openid={}, templateId={}, response={}", openid, templateId, response);
            
            JSONObject json = JSONUtil.parseObj(response);
            int errcode = json.getInt("errcode", -1);
            
            if (errcode == 0) {
                log.info("发送订阅消息成功: openid={}, templateId={}", openid, templateId);
                return true;
            } else {
                log.error("发送订阅消息失败: errcode={}, errmsg={}, openid={}, templateId={}", 
                        errcode, json.getStr("errmsg"), openid, templateId);
                return false;
            }
        } catch (Exception e) {
            log.error("发送订阅消息异常: openid={}, templateId={}, error={}", openid, templateId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 发送订阅消息（带跳转参数）
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param data 模板数据
     * @param page 跳转页面
     * @param miniprogramState 小程序版本：developer/trial/formal
     * @return 是否发送成功
     */
    public boolean sendSubscribeMessage(String openid, String templateId, Map<String, String> data, String page, String miniprogramState) {
        String token = getAccessToken();
        if (token == null) {
            log.error("发送订阅消息失败: 无法获取access_token");
            return false;
        }
        
        String url = SEND_SUBSCRIBE_MSG_URL + "?access_token=" + token;
        
        JSONObject requestBody = new JSONObject();
        requestBody.set("touser", openid);
        requestBody.set("template_id", templateId);
        if (page != null && !page.isEmpty()) {
            requestBody.set("page", page);
        }
        
        JSONObject dataObj = new JSONObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JSONObject valueObj = new JSONObject();
            valueObj.set("value", entry.getValue());
            dataObj.set(entry.getKey(), valueObj);
        }
        requestBody.set("data", dataObj);
        requestBody.set("miniprogram_state", miniprogramState != null ? miniprogramState : "formal");
        requestBody.set("lang", "zh_CN");
        
        try {
            String response = HttpUtil.post(url, requestBody.toString(), 10000);
            log.info("发送订阅消息响应: {}", response);
            
            JSONObject json = JSONUtil.parseObj(response);
            int errcode = json.getInt("errcode", -1);
            
            if (errcode == 0) {
                log.info("发送订阅消息成功: openid={}", openid);
                return true;
            } else {
                log.error("发送订阅消息失败: errcode={}, errmsg={}", errcode, json.getStr("errmsg"));
                return false;
            }
        } catch (Exception e) {
            log.error("发送订阅消息异常: {}", e.getMessage(), e);
            return false;
        }
    }

    public static class WxSession {
        private String openid;
        private String sessionKey;
        private String unionid;

        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        public String getSessionKey() { return sessionKey; }
        public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
        public String getUnionid() { return unionid; }
        public void setUnionid(String unionid) { this.unionid = unionid; }
    }
}
