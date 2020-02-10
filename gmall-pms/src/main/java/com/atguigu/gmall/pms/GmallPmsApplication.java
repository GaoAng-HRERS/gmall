package com.atguigu.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 */
@EnableDubbo
@MapperScan(basePackages = "com.atguigu.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
