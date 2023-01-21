

Project Goals

- Millions of transactions
- Load into Relation database Postgres
- RabbitMQ Streams
- Apache Kafka

Outline 

- Batching
  - Spring Batch
    - Spring Batch - CVS to Postgres (DONE)
    - Spring Batch - CVS to Mysql (DONE)
    - Spring Batch - CSV to RabbitMQ Stream (DONE)
    - Spring Batch - CSV to Apache Kafka (DONE)
  - Java Batching 
    - Spring Boot - CSV to RabbitMQ Stream (DONE)
    - Spring Boot - CSV to Apache Kafka (DONE)
- Realtime Consumption
  - Spring Cloud Stream
    - Spring Cloud Stream Consumer - Rabbit Stream - Postgres (DONE)
    - Spring Boot - RabbitMQ stream Producer - performance
  - Spring Web
    - Rest Controller Producer - Java RabbitMQ stream Producer
  - Spring Cloud Gateway
    - Gateway to Rest Controller Producer
- Partitioning
  - Super Stream