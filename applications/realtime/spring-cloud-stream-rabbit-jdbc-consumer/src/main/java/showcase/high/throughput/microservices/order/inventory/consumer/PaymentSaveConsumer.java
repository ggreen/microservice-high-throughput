package showcase.high.throughput.microservices.order.inventory.consumer;

import showcase.high.throughput.microservices.domain.Transaction;
import showcase.high.throughput.microservices.order.inventory.repository.TransactionJdbcRepository;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PaymentSaveConsumer implements Consumer<Transaction> {
    private final TransactionJdbcRepository repository;

    public PaymentSaveConsumer(TransactionJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public void accept(Transaction transaction) {
        repository.save(transaction);
    }
}
