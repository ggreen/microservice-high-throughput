package com.linkedIn.learning.transaction.batch.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.learning.throughput.domain.Transaction;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionToJsonBytesConverterTest {
    private TransactionToJsonBytesConverter subject;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        subject = new TransactionToJsonBytesConverter(objectMapper);
    }

    @Test
    void given_transaction_when_convert_then_convertJson() {

        Transaction expected = JavaBeanGeneratorCreator.of(Transaction.class).create();
        byte[] actual = subject.convert(expected);

        assertTrue(actual != null || actual.length > 0);
    }
}