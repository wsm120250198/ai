package com.baidu.springai.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baidu.springai.domain.WeChatAccessToken;
import com.baidu.springai.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.baidu.springai.config.WeChatConfig;

/**
 * 微信API工具类
 * 提供微信相关的API调用功能
 * 
 * @author baidu
 * @version 1.0
 */
@Slf4j
@Component
public class WeChatApiUtil {
    
    private static WeChatConfig weChatConfig;
    
    @Autowired
    public void setWeChatConfig(WeChatConfig weChatConfig) {
        WeChatApiUtil.weChatConfig = weChatConfig;
    }

    /**
     * 获取微信访问令牌
     */
    public static WeChatAccessToken getAccessToken(String appId, String appSecret) {
        validateParams(appId, appSecret);
        
        String url = String.format(weChatConfig.getApiTokenUrl(), appId.trim(), appSecret.trim());
        String response = HttpUtil.get(url);
        
        return parseAccessTokenResponse(response);
    }

    /**
     * 创建微信二维码
     */
    public static String createQrCode(String accessToken, int sceneId) {
        validateQrCodeParams(accessToken, sceneId);
        
        String url = String.format(weChatConfig.getApiQrcodeUrl(), accessToken.trim());
        JSONObject requestJson = buildQrCodeRequest(sceneId);
        
        String responseBody = HttpUtil.post(url, requestJson.toString());
        return parseQrCodeResponse(responseBody);
    }
    
    /**
     * 验证访问令牌参数
     */
    private static void validateParams(String appId, String appSecret) {
        if (appId == null || appId.trim().isEmpty() || appSecret == null || appSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("微信应用ID和密钥不能为空");
        }
    }
    
    /**
     * 验证二维码参数
     */
    private static void validateQrCodeParams(String accessToken, int sceneId) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("访问令牌不能为空");
        }
        if (sceneId <= 0) {
            throw new IllegalArgumentException("场景ID必须大于0");
        }
    }
    
    /**
     * 解析访问令牌响应
     */
    private static WeChatAccessToken parseAccessTokenResponse(String response) {
        JSONObject jsonObject = JSONUtil.parseObj(response);
        
        WeChatAccessToken.WeChatAccessTokenBuilder builder = WeChatAccessToken.builder();
        
        if (jsonObject.containsKey("access_token")) {
            builder.accessToken(jsonObject.getStr("access_token"));
        }
        if (jsonObject.containsKey("expires_in")) {
            builder.expiresIn(jsonObject.getInt("expires_in"));
        }
        if (jsonObject.containsKey("errcode")) {
            builder.errcode(jsonObject.getInt("errcode"));
        }
        if (jsonObject.containsKey("errmsg")) {
            builder.errmsg(jsonObject.getStr("errmsg"));
        }
        
        WeChatAccessToken tokenResponse = builder.build();
        
        if (!tokenResponse.isSuccess()) {
            log.error("获取微信访问令牌失败，错误码: {}, 错误信息: {}", 
                tokenResponse.getErrcode(), tokenResponse.getErrmsg());
        }
        
        return tokenResponse;
    }
    
    /**
     * 构建二维码请求参数
     */
    private static JSONObject buildQrCodeRequest(int sceneId) {
        JSONObject requestJson = new JSONObject();
        requestJson.set("expire_seconds", weChatConfig.getQrcodeExpireSeconds());
        requestJson.set("action_name", "QR_SCENE");
        
        JSONObject actionInfo = new JSONObject();
        JSONObject scene = new JSONObject();
        scene.set("scene_id", sceneId);
        actionInfo.set("scene", scene);
        requestJson.set("action_info", actionInfo);
        
        return requestJson;
    }
    
    /**
     * 解析二维码响应
     */
    private static String parseQrCodeResponse(String responseBody) {
        JSONObject responseJson = JSONUtil.parseObj(responseBody);
        
        if (responseJson.containsKey("ticket")) {
            return responseJson.getStr("ticket");
        } else if (responseJson.containsKey("errcode")) {
            int errCode = responseJson.getInt("errcode");
            String errMsg = responseJson.getStr("errmsg");
            log.error("微信二维码API返回错误，错误码: {}, 错误信息: {}", errCode, errMsg);
            throw BusinessException.serverError(String.format("微信二维码API错误[%d]: %s", errCode, errMsg));
        } else {
            log.error("微信二维码API返回未知格式: {}", responseBody);
            throw BusinessException.serverError("微信二维码API返回未知格式: " + responseBody);
        }
    }
}