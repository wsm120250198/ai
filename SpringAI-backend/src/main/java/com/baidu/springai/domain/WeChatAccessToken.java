package com.baidu.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信访问令牌响应实体类
 * 用于封装微信API返回的访问令牌信息
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeChatAccessToken {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌有效期（秒）
     */
    private int expiresIn;
    
    /**
     * 错误码（当请求失败时返回）
     */
    private Integer errcode;
    
    /**
     * 错误信息（当请求失败时返回）
     */
    private String errmsg;
    
    /**
     * 判断是否获取令牌成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return accessToken != null && !accessToken.isEmpty();
    }
}
