package showcase.high.throughput.microservices.transaction.batch.writer;

import showcase.high.throughput.microservices.domain.Transaction;
import com.rabbitmq.stream.ConfirmationHandler;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gregory green
 */
@Component
public class RabbitMQStreamWriter implements ItemWriter<Transaction> {

    private final Producer producer;
    private final Converter<Transaction,byte[]> serializer;
    private final ConfirmationHandler handler;

    public RabbitMQStreamWriter(Producer producer, Converter<Transaction,byte[]> serializer) {
        this.producer = producer;
        this.serializer = serializer;

        this.handler = confirmationStatus -> {};
    }

    @Override
    public void write(List<? extends Transaction> items) throws Exception {
        items.forEach(transaction ->
                        producer.send(producer.messageBuilder()
                                        .applicationProperties().entry("contentType",
                                        "application/json").messageBuilder()
                                .addData(serializer.convert(transaction)).build(),handler));
    }
}

