package com.linkedIn.learning.transaction.batch.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageBuilder;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMQStreamWriterTest {

    @Mock
    private Producer producer;
    private RabbitMQStreamWriter subject;

    @Mock
    private MessageBuilder messageBuilder;
    private Transaction expected = JavaBeanGeneratorCreator.of(Transaction.class).create();
    @Mock
    private Converter<Transaction,byte[]> serializer;

    @Mock
    private Message message;


    @Test
    void given_transaction_when_write_then_publish() throws Exception {
        var list = asList(expected);

        when(producer.messageBuilder()).thenReturn( messageBuilder);
        when(messageBuilder.addData(any())).thenReturn(messageBuilder);
        when(messageBuilder.build()).thenReturn(message);

        subject = new RabbitMQStreamWriter(producer, serializer);

        subject.write(list);

        verify(producer).send(any(),any());
    }

    @Test
    void integrationTestRabbitMQ() {

        var environment = Environment.builder().build();

        ObjectMapper objectMapper = new ObjectMapper();
        String testStreamName = "test-stream";
        try{ environment.deleteStream(testStreamName); } catch (Exception e){}
        environment.streamCreator().stream(testStreamName).create();
        int batchChunkSize = 5000;

        Producer producer = environment.producerBuilder()
                .batchSize(batchChunkSize)
                .stream(testStreamName).build();

        final int[] i = {1};
        int expectedCount = 2000000;

        Supplier<Transaction> supplier = () -> {
                if(i[0] > expectedCount)
                    return null;
                i[0]++;
                return new Transaction(valueOf(i[0]),valueOf(i[0]),null,null,0,null);

        };

        Consumer<List<Transaction>> consumer = transactionList -> {
            transactionList.forEach(transaction ->
            {
                try {
                    producer.send(producer.messageBuilder().addData(
                            objectMapper.writeValueAsBytes(transaction)).build(),null);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        };



        BatchJob batchJob = BatchJob.builder().supplier(supplier)
                .consumer(consumer)
                .batchChunkSize(batchChunkSize)
                        .build();


        BatchReport batchRecord = batchJob.execute();


        System.out.println(batchRecord+" throughput per seconds:"+batchRecord.transactionsPerMs());
    }

}