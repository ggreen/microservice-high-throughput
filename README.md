# Building High Throughput Data Microservices

*Author*: Gregory Green


Description

Learn how to design, implement, and manage high throughput microservices for modern data architectures.


Concept

Data is everywhere. Processing massive amounts of data requires microservices to be architected to meet many demands.

In this course, instructor Gregory Green walks you through how to meet increasing data demands for Microservices by building scalable high-throughput architectures.

Discover best practices for data patterns for throughput with flexible data services and multisite cloud-based use cases. Explore some of the most critical factors that affect data microservices. Dive deeper into antipatterns and the pros and cons of data technologies, with examples drawn from RabbitMQ, Postgres, Caching, and Spring. Along the way, Gregory gives you tips and pointers with hands-on demonstrations of how to successfully design and implement performant data microservices.

Outline

- Introduction
  - What are data services
  - What are data services?
  - What is throughput
  - Batch versus Realtime Event Streaming
  - 
- Batching
  - Spring batch
- Eventing Streaming 
  - RabbitMQ 
  - RabbitMQ streams 
  - Spring cloud stream 
  - Partitioning 
  - Partition with Spring and RabbitMQ 
  - Ordering 
  - Ordering with Spring and RabbitMQ 
  - RabbitMQ super streams 
- Data stores 
  - Types 
    - Relational 
      - Postgres
  - Spring Data
- APIs
  - Spring Web
  - Gateway patterns
  - Spring cloud gateway

WAN replication
- Hub and Spoke
- Rabbit site replication

Wrap Up



## Author: Gregory Green

Advisory Solution Engineer at VMware


  Gregory has worked as a senior software architect, developer, engineer, and consultant for various companies and industries ranging from finance to telecommunication and pharmaceuticals. A recognized specialist in Java and .NET C#-based data services solutions, he has extensive expertise in data management frameworks, application performance tuning, enterprise integration, application architecture, and development design patterns. He has a master of science in computer science from the New Jersey Institute of Technology and a bachelor of applied science in computer science from Stevens Institute of Technology.

 Goals

- Millions of transactions
- Load into Relation database Postgres
- RabbitMQ Streams
- Apache Kafka

Demo Outlines 

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