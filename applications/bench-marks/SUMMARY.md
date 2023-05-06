Server Specs

- Apple M1 Max 
- 10 CPU Cores
- 64 GB Ram


Processing CSV

- 2,000,000 lines
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