package com.linkedIn.learning.transaction.batch.intTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import lombok.SneakyThrows;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.conversion.Converter;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.valueOf;

public class KafkaPerfTest {
    private String topicName = "test-transactions";
    private int batchSize = 6000;

    @Test
    void kafkaPerfTest() {

        createTopic();

        var props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Object JsonSerializer;
        props.put("value.serializer", org.apache.kafka.common.serialization.ByteArraySerializer.class.getName());

        final int[] i = {1};
        int expectedCount = 2000000;

        Supplier<Transaction> supplier = () -> {
            if(i[0] > expectedCount)
                return null;
            i[0]++;
            return new Transaction(valueOf(i[0]),valueOf(i[0]),null,null,0,null);

        };

        final ObjectMapper objectMapper = new ObjectMapper();

        Converter<Transaction, byte[]> converter = transaction ->
        {
            try {
                return objectMapper.writeValueAsBytes(transaction);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };


        Producer<String, byte[]> producer = new KafkaProducer<>(props);

        Consumer<List<Transaction>> consumers = transactions -> {

            transactions.forEach(transaction -> {
                producer.send(new ProducerRecord<String, byte[]>(topicName, transaction.id(), converter.convert(transaction)));
            });

        };

        BatchJob<Transaction,Transaction> job = new BatchJob<>(supplier,consumers,batchSize);

        var report = job.execute();

        System.out.println("******\n*****\nREPORT:"+report+" transactions per Ms:"+report.transactionsPerMs());

        producer.close();
    }

    @SneakyThrows
    private void createTopic() {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (Admin admin = Admin.create(props)) {
            int partitions = 12;
            short replicationFactor = 1;
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
