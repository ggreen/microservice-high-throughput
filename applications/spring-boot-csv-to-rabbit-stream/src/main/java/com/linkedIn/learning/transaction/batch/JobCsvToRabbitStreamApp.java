package com.linkedIn.learning.transaction.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobCsvToRabbitStreamApp {

	public static void main(String[] args) {

		SpringApplication.run(
			JobCsvToRabbitStreamApp.class, args);
	}

}
