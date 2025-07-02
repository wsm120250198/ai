package com.baidu.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信二维码创建请求实体类
 * 用于封装微信二维码API的请求参数
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatQrCodeRequest {
    
    /**
     * 二维码类型：QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值
     */
    private String actionName;
    
    /**
     * 二维码详细信息
     */
    private ActionInfo actionInfo;
    
    /**
     * 该二维码有效时间，以秒为单位。最大不超过2592000（即30天）
     */
    private Integer expireSeconds;
    
    /**
     * 二维码详细信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionInfo {
        /**
         * 场景值信息
         */
        private Scene scene;
        
        /**
         * 构造方法，用于整型场景值
         * 
         * @param sceneId 场景ID
         */
        public ActionInfo(int sceneId) {
            this.scene = new Scene(sceneId);
        }
    }
    
    /**
     * 场景值内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scene {
        /**
         * 场景值ID，临时二维码时为32位非0整型
         */
        private Integer sceneId;
        
        /**
         * 场景值字符串，字符串类型，长度限制为1到64
         */
        private String sceneStr;
        
        /**
         * 构造方法，用于整型场景值
         * 
         * @param sceneId 场景ID
         */
        public Scene(int sceneId) {
            this.sceneId = sceneId;
        }
        
        /**
         * 构造方法，用于字符串场景值
         * 
         * @param sceneStr 场景字符串
         */
        public Scene(String sceneStr) {
            this.sceneStr = sceneStr;
        }
    }
}