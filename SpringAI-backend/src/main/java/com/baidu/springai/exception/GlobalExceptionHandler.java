package com.baidu.springai.exception;

import com.baidu.springai.resp.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


/**
 * 全局异常处理器
 * 统一处理应用程序中的各种异常，提供一致的错误响应格式
 * 
 * @author baidu
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
 * 
     * @param e 业务异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Response<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 - URI: {}, 错误码: {}, 错误信息: {}", 
                request.getRequestURI(), e.getCode(), e.getMessage());
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 
     * @param e 参数校验异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数校验失败");
        
        log.warn("参数校验异常 - URI: {}, 错误信息: {}", request.getRequestURI(), errorMessage);
        return Response.badRequest("参数校验失败: " + errorMessage);
    }

    /**
     * 处理约束违反异常
     * 
     * @param e 约束违反异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("约束违反");
        
        log.warn("约束违反异常 - URI: {}, 错误信息: {}", request.getRequestURI(), errorMessage);
        return Response.badRequest("参数约束违反: " + errorMessage);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Object> handleMissingParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String errorMessage = String.format("缺少必需的请求参数: %s (类型: %s)", e.getParameterName(), e.getParameterType());
        log.warn("缺少请求参数异常 - URI: {}, 错误信息: {}", request.getRequestURI(), errorMessage);
        return Response.badRequest(errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     * 
     * @param e 参数类型不匹配异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMessage = String.format("参数类型不匹配: %s，期望类型: %s", 
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配异常 - URI: {}, 错误信息: {}", request.getRequestURI(), errorMessage);
        return Response.badRequest(errorMessage);
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常 - URI: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return Response.badRequest("参数错误: " + e.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Response<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 - URI: {}, 错误信息: {}", request.getRequestURI(), e.getMessage(), e);
        return Response.serverError("系统运行异常: " + e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     * 
     * @param e 异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public Response<Object> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("未处理异常 - URI: {}, 异常类型: {}, 错误信息: {}", 
                request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage(), e);
        return Response.serverError("系统内部错误，请联系管理员");
    }


}