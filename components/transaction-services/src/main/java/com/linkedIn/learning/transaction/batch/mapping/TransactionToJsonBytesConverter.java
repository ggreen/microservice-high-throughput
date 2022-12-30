package com.linkedIn.learning.transaction.batch.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionToJsonBytesConverter implements Converter<Transaction,byte[]> {
    private final ObjectMapper objectMapper;

    public TransactionToJsonBytesConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] convert(Transaction transaction) {
        try {
            return objectMapper.writeValueAsBytes(transaction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
