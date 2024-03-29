My SQL list databases


```shell
 SHOW DATABASEs;
 use mysql;

```


Create database

```roomsql
CREATE TABLE ms_transactions  (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    details VARCHAR(20) NOT NULL
)  ENGINE=InnoDB;
```


Report on 

job_execution_id |  status   | total_time  |   tps


```roomsql
select job.job_execution_id, job.status, 
     TIMESTAMPDIFF(SECOND, start_time, end_time) as total_time_sec,
     (select step.write_count from batch_step_execution step where step.job_execution_id= job.job_execution_id) as count, 
     CONVERT(
     ( select step.write_count from batch_step_execution step where step.job_execution_id= job.job_execution_id)
     /TIMESTAMPDIFF(SECOND, start_time, end_time),char) as tps
from batch_job_execution job
where job_execution_id =
(select max(job_execution_id) from batch_job_execution);
```


Clean up table

```roomsql
truncate ms_transactions ;
```
