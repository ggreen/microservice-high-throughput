package com.vmare.order.inventory.consumer;

import showcase.high.throughput.microservices.domain.Transaction;
import com.vmare.order.inventory.repository.TransactionJdbcRepository;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class TransactionSaveConsumer implements Consumer<Transaction> {
    private final TransactionJdbcRepository repository;

    public TransactionSaveConsumer(TransactionJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public void accept(Transaction transaction) {
        repository.save(transaction);
    }
}
