package com.linkedIn.learning.transaction.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionBatchApp {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(
				SpringApplication.run(
						TransactionBatchApp.class, args)));
	}

}
