package com.atguigu.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 1、配置整合dubbo
 * 2、配置整合MyBatisPlus
 *
 *
 *
 * 请问你们项目怎么联调？
 *      联调？（前后端测试写的接口【告诉前端你发什么请求可以得到什么数据】）
 *      接口？（对于前端来说接口就是他可以调用的东西，就是发请求【Controller暴露出来的请求】）
 * Dubbo原理是什么？
 *      1、rpc原理：两个不同的服务（不同机器【不同进程】），建立连接，传输数据。
 *      2、dubbo流程图
 *
 *
 * 3、开启基于注解的事务功能  @EnableTransactionManagement
 * 4、事务的最终解决方案：
 *      1）、普通加事务。导入jdbc-starter，@EnableTransactionManagement ，加@transactional
 *      2）、方法自己调自己类里面的方法加不上事务。
 *          1）、导入aop包，开启代理对象的相关功能。
 *              <dependency>
 *                  <groupId>org.springframework.boot</groupId>
 *                  <artifactId>spring-boot-starter-aop</artifactId>
 *              </dependency>
 *          2）、加这个注解
 *              @EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
 *          3）、获取到当前类真正的代理对象，去调用方法。
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableDubbo
@MapperScan(basePackages = "com.atguigu.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}

























