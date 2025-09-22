package com.github.anqorithm.springopentelemetryjaeger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.github.anqorithm.springopentelemetryjaeger.mapper")
public class SpringOpentelemetryJaegerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOpentelemetryJaegerApplication.class, args);
    }

}
