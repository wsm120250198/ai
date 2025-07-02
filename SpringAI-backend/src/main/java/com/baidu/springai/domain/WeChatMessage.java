package com.baidu.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信消息实体类
 * 用于封装微信推送的各种类型消息数据（文本、事件等）
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeChatMessage {
    
    /**
     * 接收方微信号
     */
    private String toUserName;
    
    /**
     * 发送方微信号（OpenID）
     */
    private String fromUserName;
    
    /**
     * 消息创建时间（整型）
     */
    private String createTime;
    
    /**
     * 消息类型：text-文本消息，event-事件消息
     */
    private String msgType;
    
    /**
     * 文本消息内容
     */
    private String content;
    
    /**
     * 事件类型：subscribe-订阅，unsubscribe-取消订阅，SCAN-扫描带参数二维码
     */
    private String event;
    
    /**
     * 事件KEY值
     * 扫描带参数二维码事件：qrscene_为前缀，后面为二维码的参数值
     */
    private String eventKey;
    
    /**
     * 二维码的ticket，可用来换取二维码图片
     */
    private String ticket;
    
    /**
     * 消息ID，64位整型
     */
    private String msgId;
    
    /**
     * 图片链接（由系统生成）
     */
    private String picUrl;
    
    /**
     * 媒体文件ID
     */
    private String mediaId;
    
    /**
     * 从Map创建WeChatMessage实例
     * 
     * @param xmlMap XML解析后的Map
     * @return WeChatMessage实例
     */
    public static WeChatMessage fromMap(java.util.Map<String, String> xmlMap) {
        return WeChatMessage.builder()
                .toUserName(xmlMap.get("ToUserName"))
                .fromUserName(xmlMap.get("FromUserName"))
                .createTime(xmlMap.get("CreateTime"))
                .msgType(xmlMap.get("MsgType"))
                .content(xmlMap.get("Content"))
                .event(xmlMap.get("Event"))
                .eventKey(xmlMap.get("EventKey"))
                .ticket(xmlMap.get("Ticket"))
                .msgId(xmlMap.get("MsgId"))
                .picUrl(xmlMap.get("PicUrl"))
                .mediaId(xmlMap.get("MediaId"))
                .build();
    }
}