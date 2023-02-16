package com.vmare.order.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamRabbitJdbcConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(StreamRabbitJdbcConsumerApp.class, args);
    }
}
