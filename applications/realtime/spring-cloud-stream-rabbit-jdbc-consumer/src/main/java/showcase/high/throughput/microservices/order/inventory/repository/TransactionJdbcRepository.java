package showcase.high.throughput.microservices.order.inventory.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import showcase.high.throughput.microservices.domain.Transaction;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionJdbcRepository {

    private final JdbcTemplate template;
    private ObjectMapper objectMapper = new ObjectMapper();


    @SneakyThrows
    public void save(Transaction transaction) {
        var sql = """
                INSERT INTO payment.payments (id, details,
                contact,location, amount, timestamp) 
                VALUES (?, ?, ?, ?, ?, ?) 
                """;

        template.update(sql,
                transaction.id(),
                transaction.details(),
                transaction.contact(),
                transaction.location(),
                transaction.amount(),
                transaction.timestamp());

    }
}
