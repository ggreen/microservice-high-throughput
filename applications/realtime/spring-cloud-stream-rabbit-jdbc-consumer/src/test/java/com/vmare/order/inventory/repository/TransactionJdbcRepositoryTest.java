package com.vmare.order.inventory.repository;

import showcase.high.throughput.microservices.domain.Transaction;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionJdbcRepositoryTest {

    @Mock
    private TransactionJdbcRepository subject;
    @Mock
    private JdbcTemplate template;

    private final Transaction order = JavaBeanGeneratorCreator.of(Transaction.class).create();


    @BeforeEach
    void setUp() {
        subject = new TransactionJdbcRepository(template);
    }

    @Test
    void save() {
        subject.save(order);

        verify(template).update(anyString(),anyString(),anyString());
    }


}