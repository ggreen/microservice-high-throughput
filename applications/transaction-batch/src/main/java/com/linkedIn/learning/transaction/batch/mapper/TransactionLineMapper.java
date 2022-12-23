package com.linkedIn.learning.transaction.batch.mapper;

import com.linkedIn.learning.throughput.domain.Transaction;
import org.springframework.batch.item.file.LineMapper;

public class TransactionLineMapper implements LineMapper<Transaction> {
    @Override
    public Transaction mapLine(String csv, int i) throws Exception {
        return null;
    }
}
