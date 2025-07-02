package com.baidu.springai.config;

/**
 * 微信相关常量
 * 
 * @author baidu
 * @version 1.0
 */
public final class WeChatConstants {
    
    private WeChatConstants() {}
    
    // 二维码相关常量
    public static final int DEFAULT_QRCODE_EXPIRE_SECONDS = 604800;
    public static final String QRCODE_URL_TEMPLATE = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
    
    // API相关常量
    public static final String API_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public static final String API_QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
}