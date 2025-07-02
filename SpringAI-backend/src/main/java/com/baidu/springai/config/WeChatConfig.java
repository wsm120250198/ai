package com.baidu.springai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信配置类
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatConfig {
    
    private String appId;
    private String appSecret;
    private String token;
    
    // 可配置的值，如果配置文件中没有设置，则使用默认值
    private int qrcodeExpireSeconds = WeChatConstants.DEFAULT_QRCODE_EXPIRE_SECONDS;
    
    // 获取URL模板的方法
    public String getQrcodeUrlTemplate() {
        return WeChatConstants.QRCODE_URL_TEMPLATE;
    }
    
    public String getApiTokenUrl() {
        return WeChatConstants.API_TOKEN_URL;
    }
    
    public String getApiQrcodeUrl() {
        return WeChatConstants.API_QRCODE_URL;
    }
}