package com.ice.ice.company.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = {"com.mg.**"})
@ComponentScan("com.mg.**")
@EnableDiscoveryClient
@SpringBootApplication
public class MgCompanyWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgCompanyWebApplication.class, args);
    }

}
