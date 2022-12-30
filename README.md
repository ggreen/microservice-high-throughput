

Project Goals

- Millions of transactions
- Load into Relation database Postgres
- RabbitMQ Streams
- Apache Kafka

Outline 

- Batching
  - Spring Batch
    - Spring Batch - CVS to Postgres
    - Spring Batch - CSV to RabbitMQ Stream
    - Spring Batch - CSV to Apache Kafka
    - Spring Batch - RabbitMQ Stream to Postgres
  - Java Batching 
    - Spring Boot - CSV to RabbitMQ Stream
    - Spring Boot - CSV to Apache Kafka
    - Spring Boot - RabbitMQ Stream to Postgres
- Realtime
  - Spring Cloud Stream
    - Spring Cloud Stream Consumer - Rabbit Stream - Postgres
    - Java RabbitMQ stream Producer
  - Spring Web
    - Rest Controller Producer - Java RabbitMQ stream Producer
  - Spring Cloud Gateway
    - Gateway to Rest Controller Producer
- Partitioning
  - Super Stream