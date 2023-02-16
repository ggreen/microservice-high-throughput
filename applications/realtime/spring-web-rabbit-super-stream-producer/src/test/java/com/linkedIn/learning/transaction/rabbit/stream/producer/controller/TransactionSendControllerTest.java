package com.linkedIn.learning.transaction.rabbit.stream.producer.controller;

import com.linkedIn.learning.throughput.domain.Transaction;
import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageBuilder;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionSendControllerTest {

    @Mock
    private TransactionToJsonBytesConverter converter;

    @Mock
    private Producer producer;

    @Mock
    private MessageBuilder messageBuilder;

    @Mock
    private MessageBuilder.ApplicationPropertiesBuilder applicationPropertiesBuilder;

    @Mock
    private Message message;

    @Mock
    private MessageBuilder.PropertiesBuilder propertiesBuilder;

    private TransactionSendController subject;
    private int workerCount = 3;
    private Transaction transaction = JavaBeanGeneratorCreator.of(Transaction.class).create();

    @BeforeEach
    void setUp() {
        subject = new TransactionSendController(producer, converter);
    }

    @Test
    void send() {

        when(producer.messageBuilder()).thenReturn(messageBuilder);
        when(messageBuilder.addData(any())).thenReturn(messageBuilder);
        when(messageBuilder.properties()).thenReturn(propertiesBuilder);
        when(propertiesBuilder.messageId(anyString())).thenReturn(propertiesBuilder);
        when(propertiesBuilder.messageBuilder()).thenReturn(messageBuilder);

        when(messageBuilder.applicationProperties()).thenReturn(applicationPropertiesBuilder);
        when(applicationPropertiesBuilder.entry(anyString(),anyString())).thenReturn(applicationPropertiesBuilder);
        when(applicationPropertiesBuilder.messageBuilder()).thenReturn(messageBuilder);
        when(messageBuilder.build()).thenReturn(message);

        int count = 10;
        subject.sendTransaction(transaction);

        verify(producer).send(any(),any());
    }
}