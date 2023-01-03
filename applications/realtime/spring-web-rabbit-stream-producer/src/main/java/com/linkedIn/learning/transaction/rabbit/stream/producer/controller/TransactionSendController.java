package com.linkedIn.learning.transaction.rabbit.stream.producer.controller;

import com.linkedIn.learning.throughput.domain.Transaction;
import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Producer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transactions")
public class TransactionSendController {

    private final Producer producer;
    private final TransactionToJsonBytesConverter converter;
    private String contentType = "contentType";
    private String jsonContentType = "application/json";

    public TransactionSendController(Producer producer, TransactionToJsonBytesConverter converter) {
        this.producer = producer;
        this.converter = converter;
    }

    @PostMapping
    @RequestMapping("transaction")
    void sendTransaction(@RequestBody Transaction transaction)
    {
                producer.send(producer.messageBuilder().applicationProperties()
                        .entry(contentType,jsonContentType).messageBuilder()
                        .addData(converter.convert(transaction)).build(),h -> {});
    }

}
