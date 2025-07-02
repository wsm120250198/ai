package com.baidu.springai.service;


import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录服务接口
 * 定义微信扫码登录相关的业务操作
 * 
 * @author baidu
 * @version 1.0
 */
public interface WeiXinLoginService {
    
    /**
     * 创建二维码登录票据
     * 
     * @return 登录票据
     */
    String createQrCodeTicket();
    
    /**
     * 检查登录状态
     * 
     * @param ticket 登录票据
     * @return 用户openid或null
     */
    String checkLoginStatus(String ticket);
    
    /**
     * 保存登录状态
     * 
     * @param ticket 登录票据
     * @param openid 用户openid
     */
    void saveLoginState(String ticket, String openid);
    
    /**
     * 处理微信消息
     * 
     * @param requestBody 请求体内容
     * @param request HTTP请求对象
     * @return 响应内容
     */
    String handleWeChatMessage(String requestBody, HttpServletRequest request);
}