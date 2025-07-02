package com.baidu.springai.exception;


import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author baidu
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     * -- GETTER --
     *  获取错误码
     *
     * @return 错误码

     */
    private final Integer code;

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原因异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 构造函数（默认错误码500）
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this(500, message);
    }

    /**
     * 构造函数（默认错误码500）
     * 
     * @param message 错误信息
     * @param cause 原因异常
     */
    public BusinessException(String message, Throwable cause) {
        this(500, message, cause);
    }

    /**
     * 创建客户端错误异常（400）
     * 
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    /**
     * 创建未授权异常（401）
     * 
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    /**
     * 创建禁止访问异常（403）
     * 
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }

    /**
     * 创建资源未找到异常（404）
     * 
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /**
     * 创建服务器内部错误异常（500）
     * 
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException serverError(String message) {
        return new BusinessException(500, message);
    }

    /**
     * 创建服务器内部错误异常（500）
     * 
     * @param message 错误信息
     * @param cause 原因异常
     * @return 业务异常
     */
    public static BusinessException serverError(String message, Throwable cause) {
        return new BusinessException(500, message, cause);
    }
}