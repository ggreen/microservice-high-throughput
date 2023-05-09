# Spring Batch RabbitMQ version

The following is a summary of a local RabbitMQ Streams 
verses Apacha Kafka performance test.

This example was ran on a single developer laptop.
It uses Spring Batch for boot version: 2.7.11.


## Server Specs

- Apple M1 Max 
- 10 CPU Cores
- 64 GB Ram


## Processing CSV

The application uses the Spring Batch FlatFileItemReader to read
CSV records to convert to a Transaction domain object.

Transaction Domain record

```java
public record Transaction(String id, String details,String contact, String location, double amount, Timestamp timestamp)
        implements Serializable {
}

```


- 2,000,000 lines see 
-  ~80 Characters per line
- Single Threaded Processing
- Producers do not use acknowledgements

RabbitMQ - 3.11.12 - RabbitMQ Streams
- throughput per seconds: 544.3658138268917

Kafka  kafka_2.13-3.4.0
- throughput per seconds: 453.30915684496824

Improvement: RabbitMQ stream had a 20.09% increase


------------------
Spring Batch version 2.7
Java version 17

# RabbitMQ Stream


- CSV lines=2,000,000
- batch.chunk.size=700000
- thread count=1
- Completion time 25.5 seconds
-


| total_time   | tps      |
|--------------|----------|
| 00:00:25.543 | 78,299   |


---------

# Apache Kafka (1 ACKS)


- CSV lines=2,000,000
- batch.chunk.size=700000
- thread count=1
- spring.kafka.producer.acks=1
- Completion time 28.8 seconds

Kafka  version kafka_2.13-3.4.0


| total_time   | tps      |
|--------------|----------|
| 00:00:28.333 |  70,589   |


RabbitMQ streams had RabbitMQ streams had 10.92% increase in TPS



## Sample Code

### Apache Kafka

```java
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${batch.file.location}")
    private String fileLocation;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Value("${batch.core.pool.size}")
    private int corePoolSize;
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${batch.apache.kafka.topic.name:test-transactions}")
    private String topicName;

    @Bean
    FlatFileItemReader reader()
    {
       return  new FlatFileItemReaderBuilder<Transaction>()
               .name(applicationName+"-reader")
               .linesToSkip(1) //skip header
               .delimited()
               .names(new String[]{"id","details","contact","location","amount","timestamp"})
               .fieldSetMapper(new RecordFieldSetMapper<Transaction>(Transaction.class))
               .resource(new FileSystemResource(fileLocation))
               .build();
    }


    @Bean
    public Job importUserJob(
            JobRepository jobRepository,
                             JobExecutionListener listener, Step step1) {
        return new JobBuilder(applicationName)
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
                      ThreadPoolTaskExecutor taskExecutor) {

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

    @Bean
    KafkaItemWriter writer(KafkaTemplate<String, Transaction> template)
    {
        template.setMessageConverter(new JsonMessageConverter());
        template.setDefaultTopic(topicName);

        var writer = new KafkaItemWriter<String,Transaction>();
        writer.setKafkaTemplate(template);
        writer.setItemKeyMapper(transaction -> transaction.id() );
        return writer;
    }
}
```


### RabbitMQStreams

```java
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${batch.file.location}")
    private String fileLocation;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Value("${batch.core.pool.size}")
    private int corePoolSize;
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    FlatFileItemReader reader()
    {
       return  new FlatFileItemReaderBuilder<Transaction>()
               .name(applicationName+"-reader")
               .linesToSkip(1) //skip header
               .delimited()
               .names(new String[]{"id","details","contact","location","amount","timestamp"})
               .fieldSetMapper(new RecordFieldSetMapper<Transaction>(Transaction.class))
               .resource(new FileSystemResource(fileLocation))
               .build();
    }


    @Bean
    public Job importUserJob(
            JobRepository jobRepository,
                             JobExecutionListener listener, Step step1) {
        return new JobBuilder(applicationName)
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
                      ThreadPoolTaskExecutor taskExecutor) {

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
```

RabbitMQ Streams Writer


```java
@Component
public class RabbitMQStreamWriter implements ItemWriter<Transaction> {

    private final Producer producer;
    private final Converter<Transaction,byte[]> serializer;
    private final ConfirmationHandler handler;

    public RabbitMQStreamWriter(Producer producer, Converter<Transaction,byte[]> serializer) {
        this.producer = producer;
        this.serializer = serializer;

        this.handler = confirmationStatus -> {};
    }

    @Override
    public void write(List<? extends Transaction> items) throws Exception {
        items.forEach(transaction ->
                        producer.send(producer.messageBuilder()
                                        .applicationProperties().entry("contentType",
                                        "application/json").messageBuilder()
                                .addData(serializer.convert(transaction)).build(),handler));
    }
}
```