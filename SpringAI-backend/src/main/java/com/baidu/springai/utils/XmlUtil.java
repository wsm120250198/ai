package com.baidu.springai.utils;

import com.baidu.springai.domain.WeChatMessage;
import com.baidu.springai.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * XML处理工具类
 * 用于解析和构建微信消息的XML格式
 * 
 * @author baidu
 * @version 1.0
 */
@Slf4j
public class XmlUtil {

    /**
     * 解析微信XML消息
     * 
     * @param inputStream XML输入流
     * @return 解析后的消息Map
     * @throws BusinessException 如果解析XML失败
     */
    public static Map<String, String> parseWeChatXmlMessage(ByteArrayInputStream inputStream) {
        if (inputStream == null) {
            throw BusinessException.badRequest("XML输入流不能为空");
        }
        
        Map<String, String> xmlMap = new HashMap<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            Element root = document.getDocumentElement();
            
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getFirstChild() != null ? 
                        element.getFirstChild().getNodeValue() : "";
                    
                    xmlMap.put(nodeName, nodeValue);
                }
            }
            
            log.info("解析微信XML消息成功，消息类型: {}, 事件类型: {}", 
                xmlMap.get("MsgType"), xmlMap.get("Event"));
                
        } catch (Exception e) {
            log.error("解析微信XML消息失败", e);
            throw BusinessException.serverError("解析微信XML消息失败: " + e.getMessage());
        }
        return xmlMap;
    }
    
    /**
     * 解析消息字段
     * 
     * @param message 消息实体
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    private static void parseMessageField(WeChatMessage message, String fieldName, String fieldValue) {
        switch (fieldName) {
            case "ToUserName":
                message.setToUserName(fieldValue);
                break;
            case "FromUserName":
                message.setFromUserName(fieldValue);
                break;
            case "CreateTime":
                message.setCreateTime(fieldValue);
                break;
            case "MsgType":
                message.setMsgType(fieldValue);
                break;
            case "Content":
                message.setContent(fieldValue);
                break;
            case "Event":
                message.setEvent(fieldValue);
                break;
            case "EventKey":
                message.setEventKey(fieldValue);
                break;
            case "Ticket":
                message.setTicket(fieldValue);
                break;
            default:
                // 忽略未知字段
                break;
        }
    }

    /**
     * 构建微信XML响应消息
     * 
     * @param toUserName 接收用户
     * @param fromUserName 发送用户
     * @param content 消息内容
     * @return XML格式的响应消息
     */
    public static String buildWeChatXmlResponse(String toUserName, String fromUserName, String content) {
        long createTime = System.currentTimeMillis() / 1000;
        
        String xmlResponse = String.format(
            "<xml>" +
            "<ToUserName><![CDATA[%s]]></ToUserName>" +
            "<FromUserName><![CDATA[%s]]></FromUserName>" +
            "<CreateTime>%d</CreateTime>" +
            "<MsgType><![CDATA[text]]></MsgType>" +
            "<Content><![CDATA[%s]]></Content>" +
            "</xml>",
            toUserName, fromUserName, createTime, content
        );
        
        return xmlResponse;
    }
    
    /**
     * 构建空的微信XML响应
     * 用于不需要回复消息的场景
     * 
     * @return 空的XML响应
     */
    public static String buildEmptyWeChatXmlResponse() {
        return "success";
    }
}
