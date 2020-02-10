package com.atguigu.gmall.admin.aop;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 系统处理所有异常，给前端返回500的json
 *
 * 当我们编写环绕通知的时候，目标方法出现的异常一定要再次跑出去
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {ArithmeticException.class})
    public Object handlerException(Exception exception){
        log.error("系统全局异常感知，异常信息：{}",exception.getStackTrace());
        return new CommonResult().validateFailed("计算异常");
    }
    @ExceptionHandler(value = {NullPointerException.class})
    public Object handlerException2(Exception exception){
        log.error("系统全局异常感知，异常信息：{}",exception.getStackTrace());
        return new CommonResult().validateFailed("空指针异常");
    }
}




















