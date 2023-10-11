package showcase.high.throughput.microservices.order.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamRabbitSuperStreamConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(StreamRabbitSuperStreamConsumerApp.class, args);
    }
}
