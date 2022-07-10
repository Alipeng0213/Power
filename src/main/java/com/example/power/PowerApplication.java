package com.example.power;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class PowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowerApplication.class, args);
    }

}
