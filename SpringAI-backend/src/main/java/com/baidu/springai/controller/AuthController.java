package com.baidu.springai.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.baidu.springai.config.WeChatConfig;
import com.baidu.springai.domain.LoginStatusResponse;
import com.baidu.springai.domain.QrCodeResponse;
import com.baidu.springai.resp.Response;
import com.baidu.springai.service.WeiXinLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供扫码登录相关的API接口
 *
 * @author baidu
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private WeiXinLoginService weiXinLoginService;
    
    @Autowired
    private WeChatConfig weChatConfig;

    /**
     * 获取二维码
     */
    @GetMapping("/qrcode")
    public Response<QrCodeResponse> getQrCode() {
        String ticket = weiXinLoginService.createQrCodeTicket();
        String base64Image = generateQrCodeBase64(ticket);
        
        QrCodeResponse qrCodeResponse = QrCodeResponse.builder()
                .qrCodeId(ticket)
                .qrCodeImage(base64Image)
                .build();
        
        return Response.success("获取二维码成功", qrCodeResponse);
    }

    /**
     * 轮询登录状态
     */
    @GetMapping("/status")
    public Response<LoginStatusResponse> checkLoginStatus(@RequestParam String qrCodeId) {
        String userOpenId = weiXinLoginService.checkLoginStatus(qrCodeId.trim());
        
        if (userOpenId != null && !userOpenId.isEmpty()) {
            LoginStatusResponse.UserInfo userInfo = LoginStatusResponse.UserInfo.builder()
                    .openId(userOpenId)
                    .build();
                    
            LoginStatusResponse loginStatusResponse = LoginStatusResponse.builder()
                    .status("success")
                    .userInfo(userInfo)
                    .build();
                    
            return Response.success("登录成功", loginStatusResponse);
        } else {
            LoginStatusResponse loginStatusResponse = LoginStatusResponse.builder()
                    .status("waiting")
                    .build();
                    
            return Response.success("等待扫码", loginStatusResponse);
        }
    }
    
    /**
     * 生成二维码Base64图片
     */
    private String generateQrCodeBase64(String ticket) {
        String qrCodeUrl = String.format(weChatConfig.getQrcodeUrlTemplate(), ticket);
        byte[] imageBytes = HttpUtil.downloadBytes(qrCodeUrl);
        
        if (imageBytes == null || imageBytes.length == 0) {
            throw new RuntimeException("二维码图片获取失败");
        }
        
        return "data:image/jpeg;base64," + Base64.encode(imageBytes);
    }
}