package com.example.springbootlearn.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * 拦截所有 Controller 抛出的异常，返回统一的 Result 格式
 *
 * 执行流程：
 *   Controller 抛出异常 → 这个类捕获 → 转成 Result JSON 返回给前端
 *   而不是让浏览器看到 Spring 默认的白页错误
 */
@Slf4j  // Lombok 提供的日志对象，可以用 log.error() 记录错误
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验失败
     * 比如 @NotBlank 校验不通过时，Spring 会抛出 MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        // 提取第一条错误信息，比如 "用户名不能为空"
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + "：" + err.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");

        return Result.error(400, msg);
    }

    /**
     * 处理权限不足异常
     * 当用户尝试访问自己没有权限的接口时，Spring Security 抛出此异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.error(403, "权限不足");
    }

    /**
     * 处理所有未被特定捕获的异常（兜底）
     * 比如空指针、数据库连接失败等
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleAll(Exception e) {
        // 记录完整堆栈到日志，方便排查
        log.error("系统异常", e);
        // 只返回简短信息给前端，不暴露内部细节
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}
