package com.baidu.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录状态响应对象
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginStatusResponse {
    
    /**
     * 登录状态：waiting-等待扫码, success-登录成功, expired-已过期, error-错误
     */
    private String status;
    
    /**
     * 用户信息（登录成功时返回）
     */
    private UserInfo userInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户OpenID
         */
        private String openId;
    }
}