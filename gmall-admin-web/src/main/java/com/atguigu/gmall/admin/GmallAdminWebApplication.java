package com.atguigu.gmall.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * VO:(View Object/Value Object)视图对象；
 *      1、List<User>:把专门交给前端的数据封装成Vo；（user100个字段，userVo3个字段）RPC框架的远程传输减少序列化
 *      2、User{}；用户给我提交的数据封装成VO往下传
 *          用户注册：
 *              用户提交一个手机号；user中有100个字段;内存中创建大对象
 *              userServie.register(user)；
 *      requst---->提交的VO；
 *      response--->返回的VO；
 *
 *
 * DAO:(Database Access Object)数据库访问对象；专门对数据库进行crud的对象。XxxMapper
 * POJO:(Plain Old Java Object)古老的单纯的Java对象。JavaBean(封装数据的)
 * DO:(Data Object)数据对象-----POJO (Database Object)数据库对象(专门用来封装数据库表的实体类)
 * TO:(Transfer Object)传输对象；
 *      1、服务之间互调，为了数据传输封装对象
 *      2、aService(){
 *          user,
 *          movie
 *      }
 *      bService(用户名和电影名 UserMovieTo(userName,movieName)){
 *
 *      }
 * DTO:(Data Transfer Object)
 *
 *
 * @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 *      1、不进行数据源的配置
 * 如果导入的依赖，引入一个自动配置场景
 *  1、这个场景自动配置默认生效，我们必须配置它；
 *  2、不想配置：
 *      1、引入的时候排除这个场景
 *      2、配除掉这个场景的自动类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GmallAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallAdminWebApplication.class, args);
    }

}










































