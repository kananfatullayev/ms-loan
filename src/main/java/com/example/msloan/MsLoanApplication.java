package com.example.msloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsLoanApplication.class, args);
    }

}
