package showcase.high.throughput.microservices.transaction.rabbit.stream.producer.controller;

import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
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
    private RabbitStreamTemplate producer;

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

        int count = 10;
        subject.sendTransaction(transaction);

        verify(producer).convertAndSend(any());
    }
}