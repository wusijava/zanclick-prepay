package com.zanclick.prepay.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lvlu
 */

@ComponentScan(value = {"com.zanclick.prepay"})
@MapperScan(value = {"com.zanclick.prepay.*.mapper"})
@ServletComponentScan(basePackages = {"com.zanclick.prepay.permission.filter"})
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
