package com.linkedIn.learning.transaction.batch.writer;

import com.linkedIn.learning.throughput.domain.Transaction;
import com.rabbitmq.stream.ConfirmationHandler;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "stream",havingValue = "true")
public class RabbitMQStreamWriter implements ItemWriter<Transaction> {

    private final Producer producer;
    private final Converter<Transaction,byte[]> serializer;
    private final ConfirmationHandler handler;

    public RabbitMQStreamWriter(Producer producer, Converter<Transaction,byte[]> serializer) {
        this.producer = producer;
        this.serializer = serializer;

        this.handler = confirmationStatus -> {};
    }

//   Slower
//    @Override
//    public void write(List<? extends Transaction> items) throws Exception {
//        items.parallelStream().forEach(transaction ->
//                producer.send(producer.messageBuilder()
//                        .addData(serializer.convert(transaction)).build(),handler));
//    }

    @Override
    public void write(List<? extends Transaction> items) throws Exception {
        items.forEach(transaction ->
                        producer.send(producer.messageBuilder()
                                .addData(serializer.convert(transaction)).build(),handler));
    }
}

