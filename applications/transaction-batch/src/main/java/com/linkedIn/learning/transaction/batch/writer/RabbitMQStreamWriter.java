package com.linkedIn.learning.transaction.batch.writer;

import com.linkedIn.learning.throughput.domain.Transaction;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class RabbitMQStreamWriter implements ItemWriter<Transaction> {

    @Override
    public void write(List<? extends Transaction> items) throws Exception {

    }
}

