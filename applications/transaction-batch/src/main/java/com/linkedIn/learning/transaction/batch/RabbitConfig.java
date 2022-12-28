package com.linkedIn.learning.transaction.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.serializer.Serializer;

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
