package com.campus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园服务系统启动类
 */
@SpringBootApplication
@MapperScan("com.campus.mapper")
public class CampusServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusServiceApplication.class, args);
        System.out.println("====================================");
        System.out.println("校园服务系统启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("====================================");
    }
}
