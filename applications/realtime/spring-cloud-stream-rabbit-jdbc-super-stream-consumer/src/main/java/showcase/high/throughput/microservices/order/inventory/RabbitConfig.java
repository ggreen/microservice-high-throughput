package showcase.high.throughput.microservices.order.inventory;


import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Text;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class RabbitConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.rabbitmq.username:guest}")
    private String username = "guest";

    @Value("${spring.rabbitmq.password:guest}")
    private String password   = "guest";

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    private String hostname = "127.0.0.1";

    @Value("${spring.rabbitmq.stream.name:retail.stream.transaction}")
    private String streamName;

    @Bean
    ConnectionNameStrategy connectionNameStrategy(){
        return (connectionFactory) -> applicationName;
    }


    @Bean
    public MessageConverter converter()
    {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Environment rabbitStreamEnvironment() {

        var env = Environment.builder()
                .host(hostname)
                .username(username)
                .password(password)
                .clientProperty("id",applicationName)
                .build();

//        env.streamCreator().stream(streamName).create();

        return env;
    }

    @Bean
    Queue stream() {
        return QueueBuilder.durable(streamName)
                .stream()
                .build();
    }


    @Bean
    Consumer consumer(Environment environment)
    {
        return environment.consumerBuilder()
                .superStream(streamName)
                .name(applicationName)
                .singleActiveConsumer()
                .messageHandler((context, message) -> {
                    log.info("stream:{},",context.stream());
                    var msg = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
                    log.info(msg);
                })
                .build();
    }

}