package com.baidu.springai.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.baidu.springai.config.WeChatConfig;
import com.baidu.springai.resp.Response;
import com.baidu.springai.service.WeiXinLoginService;
import com.baidu.springai.utils.SignatureUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 微信相关控制器
 * 处理微信消息接收、验证和扫码登录功能
 *
 * @author baidu
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/wechat")
@CrossOrigin
public class WeChatController {

    @Autowired
    private WeChatConfig weChatConfig;
    
    @Autowired
    private WeiXinLoginService weiXinLoginService;

    /**
     * 微信服务器验证接口
     */
    @GetMapping("/webhook")
    public String verifyWeChatSignature(String signature, String timestamp, String nonce, String echostr) {
        return SignatureUtil.verifyWeChatSignature(signature, timestamp, nonce, weChatConfig.getToken()) 
            ? echostr : "error";
    }

    /**
     * 处理微信消息回调
     */
    @PostMapping(value = "/webhook", produces = "application/xml; charset=UTF-8")
    public String handleWeChatMessage(@RequestBody(required = false) String requestBody, HttpServletRequest request) {
        return weiXinLoginService.handleWeChatMessage(requestBody, request);
    }

    /**
     * 生成微信扫码登录票据
     */
    @GetMapping("/qrcode/ticket")
    public Response<String> generateQrCodeTicket() {
        String ticket = weiXinLoginService.createQrCodeTicket();
        return Response.success(ticket);
    }

    /**
     * 获取二维码图片（字节流形式）
     */
    @GetMapping("/qrcode/image")
    public ResponseEntity<byte[]> getQrCodeImageBytes(@RequestParam String ticket) {
        byte[] imageBytes = downloadQrCodeImage(ticket);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);

        return ResponseEntity.ok().headers(headers).body(imageBytes);
    }

    /**
     * 获取二维码图片（Base64形式）
     */
    @GetMapping("/qrcode/base64")
    public Response<String> getQrCodeImageBase64(@RequestParam String ticket) {
        byte[] imageBytes = downloadQrCodeImage(ticket);
        String base64Image = "data:image/jpeg;base64," + Base64.encode(imageBytes);
        return Response.success("获取二维码图片成功", base64Image);
    }

    /**
     * 检查扫码登录状态
     */
    @GetMapping("/login/status")
    public Response<String> checkLoginStatus(@RequestParam String ticket) {
        String userOpenId = weiXinLoginService.checkLoginStatus(ticket.trim());
        if (userOpenId != null && !userOpenId.isEmpty()) {
            return Response.success("登录状态检查成功", userOpenId);
        }
        throw new RuntimeException("未登录或登录超时");
    }
    
    /**
     * 下载二维码图片的通用方法
     */
    private byte[] downloadQrCodeImage(String ticket) {
        if (ticket == null || ticket.trim().isEmpty()) {
            throw new IllegalArgumentException("票据参数不能为空");
        }
        
        String qrCodeUrl = String.format(weChatConfig.getQrcodeUrlTemplate(), ticket.trim());
        byte[] imageBytes = HttpUtil.downloadBytes(qrCodeUrl);
        
        if (imageBytes == null || imageBytes.length == 0) {
            throw new RuntimeException("二维码图片不存在或已过期");
        }
        
        return imageBytes;
    }
}
