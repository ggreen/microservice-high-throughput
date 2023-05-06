
Report on 

job_execution_id |  status   | total_time  |   tps


```roomsql
select job.job_execution_id, job.status, job.end_time - job.start_time as total_time,
     to_char(
     ( select step.write_count from batch_step_execution step where step.job_execution_id= job.job_execution_id)
     /(extract(epoch from end_time)  - extract(epoch from start_time)),'FM9,999,999') as tps
from batch_job_execution job
where job_execution_id =
(select max(job_execution_id) from batch_job_execution);
```


Clean up table

------------------
Spring Batch version 2.7
Java version 17

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
