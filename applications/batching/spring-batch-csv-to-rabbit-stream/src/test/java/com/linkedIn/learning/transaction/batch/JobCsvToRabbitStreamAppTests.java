package com.linkedIn.learning.transaction.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class JobCsvToRabbitStreamAppTests {

	@Test
	@EnabledIfSystemProperty(named = "intTest",matches = "true")
	void contextLoads() {
	}

}
