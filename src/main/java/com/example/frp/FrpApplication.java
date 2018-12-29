package com.example.frp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class FrpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrpApplication.class, args);
    }

    @RequestMapping("/")
    public String frp() {
        return "login/view";
    }
}
