package showcase.high.throughput.microservices.transaction.batch;

import org.springframework.core.io.UrlResource;
import showcase.high.throughput.microservices.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.MalformedURLException;

@Configuration
@EnableBatchProcessing
@EnableTask
public class BatchConfig {

    @Value("${batch.file.location}")
    private String fileLocation;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Value("${batch.core.pool.size}")
    private int corePoolSize;

    @Bean
    FlatFileItemReader reader() throws MalformedURLException {
       return  new FlatFileItemReaderBuilder<Transaction>()
               .name("transactionReader")
               .linesToSkip(1) //skip header
               .delimited()
               .names(new String[]{"id","details","contact","location","amount","timestamp"})
               .fieldSetMapper(new RecordFieldSetMapper<Transaction>(Transaction.class))
               .resource(new UrlResource(fileLocation))
               .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO ms_transactions (id, details) VALUES (:id, :details)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(
            JobRepository jobRepository,
                             JobExecutionListener listener, Step step1) {
        return new JobBuilder("importTransactions")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .repository(jobRepository)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    JobExecutionListener listener(JdbcTemplate jdbcTemplate)
    {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                jdbcTemplate.update("truncate ms_transactions");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
            }
        };
    }
    @Bean
    public Step step1(ItemWriter<Transaction> writer,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ThreadPoolTaskExecutor taskExecutor) throws MalformedURLException {

        taskExecutor.setCorePoolSize(corePoolSize);

        return new StepBuilder("step1")
                .transactionManager(transactionManager)
                .repository(jobRepository)
                .<Transaction, Transaction> chunk(chunkSize)
                .reader(reader())
                .writer(writer)
                .taskExecutor(taskExecutor)
                .build();
    }

}
