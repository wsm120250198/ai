package com.baidu.springai.service.impl;

import com.baidu.springai.config.WeChatConfig;
import com.baidu.springai.domain.WeChatAccessToken;
import com.baidu.springai.domain.WeChatMessage;
import com.baidu.springai.exception.BusinessException;
import com.baidu.springai.service.WeiXinLoginService;
import com.baidu.springai.utils.WeChatApiUtil;
import com.baidu.springai.utils.XmlUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录服务实现类
 * 实现微信扫码登录相关的业务逻辑
 * 
 * @author baidu
 * @version 1.0
 */
@Service
@Slf4j
public class WeiXinLoginServiceImpl implements WeiXinLoginService {

    @Autowired
    private WeChatConfig weChatConfig;
    
    private final Map<String, String> loginStateMap = new HashMap<>();
    // 添加场景ID到票据的映射
    private final Map<String, String> sceneToTicketMap = new HashMap<>();

    @Override
    public String createQrCodeTicket() {
        WeChatAccessToken accessToken = WeChatApiUtil.getAccessToken(weChatConfig.getAppId(), weChatConfig.getAppSecret());
        if (!accessToken.isSuccess()) {
            throw new BusinessException("获取微信access_token失败: " + accessToken.getErrmsg());
        }

        int sceneId = generateUniqueSceneId();
        String ticket = WeChatApiUtil.createQrCode(accessToken.getAccessToken(), sceneId);
        
        // 建立场景ID和票据的映射关系
        sceneToTicketMap.put(String.valueOf(sceneId), ticket);
        // 初始化登录状态
        loginStateMap.put(ticket, null);
        
        return ticket;
    }
    
    @Override
    public String checkLoginStatus(String ticket) {
        return loginStateMap.get(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) {
        loginStateMap.put(ticket, openid);
        log.info("保存登录状态: ticket={}, openid={}", ticket, openid);
    }

    @Override
    public String handleWeChatMessage(String requestBody, HttpServletRequest request) {
        if (requestBody == null || requestBody.isEmpty()) {
            String echostr = request.getParameter("echostr");
            return echostr != null ? echostr : "success";
        }

        WeChatMessage message = parseWeChatMessage(requestBody);
        
        if ("event".equals(message.getMsgType())) {
            return handleEventMessage(message);
        } else if ("text".equals(message.getMsgType())) {
            return XmlUtil.buildWeChatXmlResponse(
                message.getFromUserName(), message.getToUserName(), 
                "你发送的内容是：" + message.getContent());
        } else {
            return XmlUtil.buildWeChatXmlResponse(
                message.getFromUserName(), message.getToUserName(), "已收到您的消息");
        }
    }
    
    /**
     * 解析微信消息
     */
    private WeChatMessage parseWeChatMessage(String requestBody) {
        Map<String, String> xmlMap = XmlUtil.parseWeChatXmlMessage(
            new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        return WeChatMessage.fromMap(xmlMap);
    }
    
    /**
     * 处理事件消息
     */
    private String handleEventMessage(WeChatMessage message) {
        String event = message.getEvent();
        String eventKey = message.getEventKey();
        String fromUser = message.getFromUserName();
        String toUser = message.getToUserName();
        
        if (eventKey != null && !eventKey.isEmpty()) {
            if ("SCAN".equalsIgnoreCase(event) || 
                ("subscribe".equalsIgnoreCase(event) && eventKey.startsWith("qrscene_"))) {
                
                String sceneValue = "subscribe".equalsIgnoreCase(event) ? 
                    eventKey.substring("qrscene_".length()) : eventKey;
                
                // 通过场景ID找到对应的票据
                String ticket = sceneToTicketMap.get(sceneValue);
                if (ticket != null) {
                    saveLoginState(ticket, fromUser);
                    return XmlUtil.buildWeChatXmlResponse(fromUser, toUser, "登录操作成功，请返回网页查看状态");
                }
            }
        }
        
        return switch (event.toLowerCase()) {
            case "subscribe" -> XmlUtil.buildWeChatXmlResponse(fromUser, toUser, "感谢您的关注！");
            default -> XmlUtil.buildEmptyWeChatXmlResponse();
        };
    }
    
    /**
     * 生成唯一的场景ID
     */
    private int generateUniqueSceneId() {
        return Math.max((int) (System.currentTimeMillis() % 1_000_000_000L), 1);
    }
}
