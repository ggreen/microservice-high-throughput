package com.vmare.order.inventory.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.domain.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static nyla.solutions.core.util.Organizer.toMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionJdbcRepository {

    private final JdbcTemplate template;
    private ObjectMapper objectMapper = new ObjectMapper();


    @SneakyThrows
    public void save(Transaction transaction) {
        var sql = """
                INSERT INTO ms_transactions (id, details) 
                VALUES (?, ?) 
                """;

        template.update(sql,
                transaction.id(),
                transaction.details());

    }
}
