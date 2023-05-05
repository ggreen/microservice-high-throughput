package com.linkedIn.learning.transaction.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

@Configuration
public class KafkaConfig {

    private int batchSize = 10000;
    private Integer lingerMs = 20;
    private String topicName= "test-transactions";

    private int partitions = 1;
    private short replicationFactor = 1;


    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }


    @Bean
    Producer<String, byte[]> producer()
    {

        createTopic();


        var props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("linger.ms", lingerMs.toString());
        //props.put("acks", "all");
        props.put("acks", "0");
        props.put("batch.size",String.valueOf(batchSize));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", org.apache.kafka.common.serialization.ByteArraySerializer.class.getName());


        return new KafkaProducer<>(props);
    }


    @Bean
    Consumer<List<Transaction>> consumers(Producer<String, byte[]> producer, TransactionToJsonBytesConverter converter) {
        return transactions -> {

            transactions.forEach(transaction -> {
                producer.send(new ProducerRecord<String, byte[]>(topicName, transaction.id(), converter.convert(transaction)));
            });
        };
    }


    @SneakyThrows
    private void createTopic() {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (Admin admin = Admin.create(props)) {

            try {
                var deleteResults = admin.deleteTopics(asList(topicName));
                deleteResults.all().get();
            }
            catch(Exception e){}


            // Create a compacted topic
            CreateTopicsResult result = admin.createTopics(Collections.singleton(
                    new NewTopic(topicName, partitions, replicationFactor)
                            .configs(Collections.singletonMap(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT))));

            // Call values() to get the result for a specific topic
            KafkaFuture<Void> future = result.values().get(topicName);

            // Call get() to block until the topic creation is complete or has failed
            // if creation failed the ExecutionException wraps the underlying cause.
            future.get();
        }
        catch (ExecutionException e){
            if(!e.getMessage().contains("TopicExistsException"))
                throw e;
        }
    }

}
