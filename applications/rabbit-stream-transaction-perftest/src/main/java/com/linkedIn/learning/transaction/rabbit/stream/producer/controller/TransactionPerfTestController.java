package com.linkedIn.learning.transaction.rabbit.stream.producer.controller;

import com.linkedIn.learning.throughput.domain.Transaction;
import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.creational.generator.FullNameCreator;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("transactions/perfTest")
public class TransactionPerfTestController {

    private FullNameCreator fullNameCreator = new FullNameCreator();
    private String[] zipcodes = {"00001","00002", "00003", "00004","00005" };
    private String contentType = "contentType";
    private String jsonContentType = "application/json";

    private final Producer producer;
    private final TransactionToJsonBytesConverter converter;
    private final int workerCount;

    public TransactionPerfTestController(Producer producer, TransactionToJsonBytesConverter converter, @Value("${app.worker.count}") int workerCount) {
        this.producer = producer;
        this.converter = converter;
        this.workerCount = workerCount;
    }

    @PostMapping("{count}")
    void perfTest(@RequestParam String prefix, @PathVariable int count)
    {
        var boss = new ExecutorBoss(workerCount);
        List<Future> futures = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            var transaction = new Transaction(prefix+i,
                    "details-"+prefix+i,
                    fullNameCreator.create(),
                    zipcodes[i%zipcodes.length],
                    Double.valueOf(i),
                    new Timestamp(System.currentTimeMillis())
                    );

            futures.add(boss.startWorking(() -> {
                producer.send(producer.messageBuilder().applicationProperties()
                        .entry(contentType,jsonContentType).messageBuilder()
                        .addData(converter.convert(transaction)).build(),h -> {});
            }));
        }

        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
