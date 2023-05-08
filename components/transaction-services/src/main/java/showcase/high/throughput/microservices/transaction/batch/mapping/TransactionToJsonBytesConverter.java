package showcase.high.throughput.microservices.transaction.batch.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.domain.Transaction;
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
