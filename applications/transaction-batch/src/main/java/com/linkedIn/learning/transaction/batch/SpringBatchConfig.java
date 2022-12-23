package com.linkedIn.learning.transaction.batch;

import com.linkedIn.learning.throughput.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Types;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Value("${batch.file.location}")
    private String fileLocation;

    @Bean
    FlatFileItemReader reader()
    {
       return  new FlatFileItemReaderBuilder<Transaction>()
               .name("transactionReader")
               .linesToSkip(1) //skip header
               .delimited()
               .names(new String[]{"id","details","contact","location","amount","timestamp"})
               .fieldSetMapper(new RecordFieldSetMapper<Transaction>(Transaction.class))
               .resource(new FileSystemResource(fileLocation))
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
//            @Qualifier("throughputJob")
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

//    @Bean("throughputJob")
//    @Primary
//    JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager tm)
//    {
//        var jobRepo = new JobRepositoryFactoryBean();
//        jobRepo.setClobType(Types.VARCHAR);
//        jobRepo.setDataSource(dataSource);
//        jobRepo.setTransactionManager(tm);
//        jobRepo.setDatabaseType(DatabaseType.POSTGRES.name());
//
//        try {
//            jobRepo.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//        return jobRepo;
//    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Transaction> writer,
//                      @Qualifier("throughputJob")
                      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1")
                .repository(jobRepository)
                .transactionManager(transactionManager)

                .<Transaction, Transaction> chunk(10)
                .reader(reader())
//                .processor(processor())
                .writer(writer)
                .build();
    }


}
