package com.baidu.springai.utils;

import com.baidu.springai.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信签名验证工具类
 * 用于验证微信服务器发送的消息签名
 * 
 * @author baidu
 * @version 1.0
 */
@Slf4j
public class SignatureUtil {

    /**
     * 验证微信签名
     * 
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param token 开发者令牌
     * @return 验证结果
     */
    public static boolean verifyWeChatSignature(String signature, String timestamp, String nonce, String token) {
        if (signature == null || timestamp == null || nonce == null || token == null) {
            log.warn("微信签名验证参数不完整");
            return false;
        }
        
        // 将token、timestamp、nonce三个参数进行字典序排序
        String[] params = {token, timestamp, nonce};
        Arrays.sort(params);
        
        // 将三个参数字符串拼接成一个字符串进行sha1加密
        StringBuilder content = new StringBuilder();
        for (String param : params) {
            content.append(param);
        }
        
        String encryptedStr = sha1Encrypt(content.toString());
        
        // 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        boolean isValid = encryptedStr != null && encryptedStr.equals(signature.toUpperCase());
        
        if (isValid) {
            log.info("微信签名验证成功");
        } else {
            log.warn("微信签名验证失败，期望: {}, 实际: {}", encryptedStr, signature);
        }
        
        return isValid;
    }
    
    /**
     * SHA1加密
     * 
     * @param content 待加密内容
     * @return 加密后的字符串
     * @throws BusinessException 如果加密过程中出现错误
     */
    private static String sha1Encrypt(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.getBytes());
            return bytesToHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA1加密算法不存在", e);
            // 这里不抛出异常，因为验证签名方法需要返回false而不是抛出异常
            return null;
        }
    }

    /**
     * 字节数组转换为十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(byteToHex(b));
        }
        return result.toString();
    }

    /**
     * 单个字节转换为十六进制字符串
     * 
     * @param b 字节
     * @return 十六进制字符串
     */
    private static String byteToHex(byte b) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[2];
        hexChars[0] = hexDigits[(b >>> 4) & 0x0F];
        hexChars[1] = hexDigits[b & 0x0F];
        return new String(hexChars);
    }
}
