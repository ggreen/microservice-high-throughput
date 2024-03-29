package showcase.high.throughput.microservices.transaction.batch.writer;

import showcase.high.throughput.microservices.domain.Transaction;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageBuilder;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}