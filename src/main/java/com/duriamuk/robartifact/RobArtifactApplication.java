package com.duriamuk.robartifact;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@SpringBootApplication
@ComponentScan(basePackages = "com.duriamuk.robartifact")
@MapperScan(basePackages = "com.duriamuk.robartifact.mapper")
@Controller
public class RobArtifactApplication {
    public static void main(String[] args) {
        SpringApplication.run(RobArtifactApplication.class, args);

//        Calculater.buildPositionList();
    }

    @RequestMapping("/")
    public String robArtifact() {
        return "login/view";
    }
}
