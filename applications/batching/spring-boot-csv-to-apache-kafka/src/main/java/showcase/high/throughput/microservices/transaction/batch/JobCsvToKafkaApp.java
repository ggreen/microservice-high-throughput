package showcase.high.throughput.microservices.transaction.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobCsvToKafkaApp {
    public static void main(String[] args) {
        SpringApplication.run(JobCsvToKafkaApp.class);
    }
}
