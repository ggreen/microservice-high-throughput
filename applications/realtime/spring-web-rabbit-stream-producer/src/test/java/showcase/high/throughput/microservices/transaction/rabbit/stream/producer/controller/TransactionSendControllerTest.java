package showcase.high.throughput.microservices.transaction.rabbit.stream.producer.controller;

import showcase.high.throughput.microservices.domain.Transaction;
import showcase.high.throughput.microservices.transaction.batch.mapping.TransactionToJsonBytesConverter;
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
    private MessageBuilder builder;
    @Mock
    private MessageBuilder.ApplicationPropertiesBuilder properties;

    @Mock
    private Message message;
    private TransactionSendController subject;
    private int workerCount = 3;
    private Transaction transaction = JavaBeanGeneratorCreator.of(Transaction.class).create();


    @BeforeEach
    void setUp() {
        subject = new TransactionSendController(producer, converter);
    }

    @Test
    void send() {

        when(producer.messageBuilder()).thenReturn(builder);
        when(builder.addData(any())).thenReturn(builder);
        when(builder.applicationProperties()).thenReturn(properties);
        when(properties.entry(anyString(),anyString())).thenReturn(properties);
        when(properties.messageBuilder()).thenReturn(builder);
        when(builder.build()).thenReturn(message);

        int count = 10;
        subject.sendTransaction(transaction);

        verify(producer).send(any(),any());
    }
}