package showcase.high.throughput.microservices.transaction.batch.intTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.domain.Transaction;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.conversion.io.SerializableToBytesConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.valueOf;


public class RabbitMQPerfTest {

    @Test
    @EnabledIfSystemProperty(named = "intTest", matches = "true")
    void rabbitMQ_perfTest_with_json_serialization() {
        final ObjectMapper objectMapper = new ObjectMapper();

        Converter<Transaction, byte[]> converter = transaction ->
        {
            try {
                return objectMapper.writeValueAsBytes(transaction);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        rabbitPerfTest(converter);
    }

    @Test
    @EnabledIfSystemProperty(named = "intTest", matches = "true")
    void integrationRabbitMQ_java_serialization()
    {
        rabbitPerfTest(new SerializableToBytesConverter<Transaction>());
    }

    void rabbitPerfTest(Converter<Transaction,byte[]> converter) {

        var environment = Environment.builder().build();

        ObjectMapper objectMapper = new ObjectMapper();
        String testStreamName = "test-stream";
        try{ environment.deleteStream(testStreamName); } catch (Exception e){}
        environment.streamCreator().stream(testStreamName).create();
        int batchChunkSize = 5000;

        Producer producer = environment.producerBuilder()
                .batchSize(batchChunkSize)
                .stream(testStreamName).build();

        final int[] i = {1};
        int expectedCount = 2000000;

        Supplier<Transaction> supplier = () -> {
            if(i[0] > expectedCount)
                return null;
            i[0]++;
            return new Transaction(valueOf(i[0]),valueOf(i[0]),null,null,0,null);

        };

        Consumer<List<Transaction>> consumer = transactionList -> {
            transactionList.forEach(transaction ->
            {
                producer.send(producer.messageBuilder().addData(
                        converter.convert(transaction)).build(),null);
            });
        };

        BatchJob batchJob = BatchJob.builder().supplier(supplier)
                .consumer(consumer)
                .batchChunkSize(batchChunkSize)
                .build();
        BatchReport batchRecord = batchJob.execute();
        System.out.println(batchRecord+" throughput per seconds:"+batchRecord.transactionsPerMs());
    }
}
