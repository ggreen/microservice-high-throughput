package com.linkedIn.learning.transaction.rabbit.stream.producer.controller;

import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageBuilder;
import com.rabbitmq.stream.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionPerfTestControllerTest {

    @Mock
    private TransactionToJsonBytesConverter converter;

    @Mock
    private Producer producer;

    @Mock
    private MessageBuilder builder;
    @Mock
    private MessageBuilder.ApplicationPropertiesBuilder properties;

    @Mock
    private Message message;
    private TransactionPerfTestController subject;

    @BeforeEach
    void setUp() {
        subject = new TransactionPerfTestController(producer, converter,3);
    }

    @Test
    void perfTest() {

        when(producer.messageBuilder()).thenReturn(builder);
        when(builder.addData(any())).thenReturn(builder);
        when(builder.applicationProperties()).thenReturn(properties);
        when(properties.entry(anyString(),anyString())).thenReturn(properties);
        when(properties.messageBuilder()).thenReturn(builder);
        when(builder.build()).thenReturn(message);

        int count = 10;
        subject.perfTest("junit",count);

        verify(producer,times(count)).send(any(),any());
    }
}