package com.gym.crm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CrmGymApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmGymApplication.class, args);
    }
}
