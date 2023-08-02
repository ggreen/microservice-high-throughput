package showcase.high.throughput.microservices.transaction.batch.runner;

import showcase.high.throughput.microservices.domain.Payment;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
public class BatchJobRunner implements ApplicationRunner {
    private final Supplier<Payment> supplier;
    private final Consumer<List<Payment>> consumer;

    private final int batchChunkSize;

    public BatchJobRunner(Supplier<Payment> supplier,
                          Consumer<List<Payment>> consumer,
                          @Value("${batch.chunk.size}")
                          int batchChunkSize) {
        this.supplier = supplier;
        this.consumer = consumer;
        this.batchChunkSize = batchChunkSize;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        BatchJob batchJob = BatchJob.builder().supplier(supplier)
                .consumer(consumer)
                .batchChunkSize(batchChunkSize)
                .build();
        BatchReport batchRecord = batchJob.execute();

        log.info("{} throughput per seconds: {} ",batchRecord,batchRecord.transactionsPerMs());
    }
}
