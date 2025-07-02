package com.baidu.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码响应对象
 * 
 * @author baidu
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResponse {
    
    /**
     * 二维码ID（票据）
     */
    private String qrCodeId;
    
    /**
     * 二维码图片Base64编码
     */
    private String qrCodeImage;
}