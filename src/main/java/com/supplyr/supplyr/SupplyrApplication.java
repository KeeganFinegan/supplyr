package com.supplyr.supplyr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupplyrApplication {


    public static void main(String[] args) {
        SpringApplication.run(SupplyrApplication.class, args);
    }
}


