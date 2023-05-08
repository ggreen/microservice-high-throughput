package showcase.high.throughput.microservices.transaction.rabbit.stream.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
@ComponentScan(basePackageClasses = TransactionToJsonBytesConverter.class)
public class RabbitConfig {

    @Value("${rabbitmq.stream.name:transaction}")
    private String streamName;


    @Value("${rabbitmq.stream.producer.batch.size:500}")
    private int batchSize;


    @Primary
    @Bean
    ObjectMapper objectMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        // StdDateFormat is ISO8601 since jackson 2.9
        String pattern = "yyyy-MM-dd HH:mm:ss.SSSSS";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        mapper.setDateFormat(dateFormat);

        return mapper;
    }

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
    Producer producer(Environment environment)
    {
        return environment.producerBuilder().stream(streamName)
                //.batchSize(batchSize)
                .build();
    }
}
