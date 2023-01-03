package com.linkedIn.learning.transaction.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
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

        try{ env.deleteStream(streamName); } catch (Exception e){}
        env.streamCreator().stream(streamName).create();

        return env;
    }

    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }


    @Bean
    Producer producer(Environment environment)
    {
        return environment.producerBuilder().stream(streamName)
                //.batchSize(batchSize)
                .build();
    }
}
