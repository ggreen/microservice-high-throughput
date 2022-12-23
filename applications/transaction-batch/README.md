
```roomsql
select end_time - start_time as total_time,
    2000000/(extract(epoch from end_time)  - extract(epoch from start_time)) as tps
from batch_job_execution
where job_execution_id =
(select max(job_execution_id) from batch_job_execution);
```
