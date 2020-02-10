package com.atguigu.gmall.admin.aop;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 *编写切面
 * 1、导入切面场景
 * 2、编写切面
 *      1、@Aspec
 *      2、切入点表达式
 *
 *      3、通知
 *          前置通知：方法执行之前触发
 *          后置通知：方法执行之后触发
 *          返回通知：方法正常返回之后触发
 *          异常通知：方法出现异常触发
 *          环绕通知：4合1
 *
 *
 *
 * 利用aop完成统一的数据校验，数据校验出错就返回给前端错误提示
 */
@Slf4j
@Aspect
@Component
public class DataValidAspect {

    @Around("execution(* com.atguigu.gmall.admin..*Controller.*(..))")
    public Object validAround(ProceedingJoinPoint point){
        Object proceed = null;
        //就是我们反射的 method.invoke();
        try {
            Object[] args = point.getArgs();
            for (Object obj : args) {
                BindingResult r = (BindingResult) obj;
                if(r.getErrorCount()>0){
                    //框架自动校验检测到错误
                    return new CommonResult().validateFailed(r);
                }
            }

            //System.out.println("前置通知");
            proceed = point.proceed(point.getArgs());
            //System.out.println("返回通知");
            log.debug("校验切面将目标方法方形...{}",proceed);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
            //System.out.println("异常通知");
        }finally {
            System.out.println("后置通知");
        }
        return  proceed;
    }
}
