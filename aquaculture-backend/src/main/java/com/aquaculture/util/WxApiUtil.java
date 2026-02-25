package com.aquaculture.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WxApiUtil {

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 通过code获取openid和session_key
     */
    public WxSession code2Session(String code) {
        String url = JSCODE2SESSION_URL + "?appid=" + appid 
                + "&secret=" + secret 
                + "&js_code=" + code 
                + "&grant_type=authorization_code";
        
        try {
            String response = HttpUtil.get(url, 5000);
            log.info("微信登录响应: {}", response);
            
            JSONObject json = JSONUtil.parseObj(response);
            
            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                log.error("微信登录失败: {}", json.getStr("errmsg"));
                return null;
            }
            
            WxSession session = new WxSession();
            session.setOpenid(json.getStr("openid"));
            session.setSessionKey(json.getStr("session_key"));
            session.setUnionid(json.getStr("unionid"));
            return session;
        } catch (Exception e) {
            log.error("调用微信API异常", e);
            return null;
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
