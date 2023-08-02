package showcase.high.throughput.microservices.transaction.batch;

import showcase.high.throughput.microservices.domain.Payment;
import showcase.high.throughput.microservices.transaction.batch.mapping.CsvToTransactionConverter;
import showcase.high.throughput.microservices.transaction.batch.mapping.TransactionToJsonBytesConverter;
import showcase.high.throughput.microservices.transaction.batch.runner.BatchJobRunner;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.io.csv.supplier.CsvConverterSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@ComponentScan(basePackageClasses = {BatchJobRunner.class})
public class BatchConfig {

    @Value("${batch.file.location}")
    private String filePath ;

    @Bean
    CsvReader csvReader() throws IOException {
        return new CsvReader(Paths.get(filePath).toFile());
    }
    @Bean
    Supplier<Payment> supplier(CsvReader reader, CsvToTransactionConverter converter)
    {
        var supplier= new CsvConverterSupplier(reader,converter);
        supplier.skipLines(1);
        return supplier;
    }

    @Bean
    Consumer<List<Payment>> consumer(Producer producer, TransactionToJsonBytesConverter converter)
    {
        return transactionList -> {
            transactionList.forEach(transaction ->
            {
                producer.send(producer.messageBuilder().addData(
                        converter.convert(transaction)).build(),null);
            });
        };
    }
}
