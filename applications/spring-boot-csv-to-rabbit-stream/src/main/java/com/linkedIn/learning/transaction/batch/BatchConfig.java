package com.linkedIn.learning.transaction.batch;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import com.linkedIn.learning.throughput.domain.Transaction;
import com.linkedIn.learning.transaction.batch.mapping.CsvToTransactionConverter;
import com.linkedIn.learning.transaction.batch.mapping.TransactionToJsonBytesConverter;
import com.rabbitmq.stream.Producer;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.io.csv.supplier.CsvConverterSupplier;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class BatchConfig {

    @Value("${batch.file.location}")
    private String filePath ;

    @Bean
    CsvReader csvReader() throws IOException {
        return new CsvReader(Paths.get(filePath).toFile());
    }
    @Bean
    Supplier<Transaction> supplier(CsvReader reader, CsvToTransactionConverter converter)
    {
        var supplier= new CsvConverterSupplier(reader,converter);
        supplier.skipLines(1);
        return supplier;
    }

    @Bean
    Consumer<List<Transaction>> consumer(Producer producer, TransactionToJsonBytesConverter converter)
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
