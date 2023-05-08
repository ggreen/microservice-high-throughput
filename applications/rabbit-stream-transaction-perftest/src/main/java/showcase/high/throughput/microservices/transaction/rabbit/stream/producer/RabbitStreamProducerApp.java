package showcase.high.throughput.microservices.transaction.rabbit.stream.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitStreamProducerApp {

	public static void main(String[] args) {
		SpringApplication.run(RabbitStreamProducerApp.class, args);
	}

}
