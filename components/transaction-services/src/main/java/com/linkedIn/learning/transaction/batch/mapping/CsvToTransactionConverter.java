package com.linkedIn.learning.transaction.batch.mapping;

import com.linkedIn.learning.throughput.domain.Transaction;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CsvToTransactionConverter implements Converter<List<String>, Transaction> {
    @Override
    public Transaction convert(List<String> csvRow) {
        return new Transaction(
                csvRow.get(0), //id
                csvRow.get(1), //detials
                csvRow.get(2), //contact
                csvRow.get(3), //location
                Double.valueOf(csvRow.get(4)), //amount
                Timestamp.valueOf(csvRow.get(5))
        );
    }
}
