package showcase.high.throughput.microservices.transaction.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.domain.Transaction;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.stream.name:transaction}")
    private String streamName;


    @Value("${rabbitmq.stream.producer.batch.size:500}")
    private int batchSize;

    @Bean
    Environment rabbitEnv()
    {
        var env = Environment.builder().build();
        env.streamCreator().stream(streamName).create();

        return env;
    }

    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }

    @Bean
    Converter<Transaction,byte[]> serializer(ObjectMapper objectMapper)
    {
        return transaction -> {
            try {
                return objectMapper.writeValueAsBytes(transaction);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    Producer producer(Environment environment)
    {
        return environment.producerBuilder().stream(streamName)
                //.batchSize(batchSize)
                .build();
    }
}
