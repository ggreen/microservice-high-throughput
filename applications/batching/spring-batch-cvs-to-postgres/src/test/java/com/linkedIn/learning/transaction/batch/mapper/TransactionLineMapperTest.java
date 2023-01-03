package com.linkedIn.learning.transaction.batch.mapper;

import com.linkedIn.learning.throughput.domain.Transaction;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.*;

class TransactionLineMapperTest {

//    private TransactionLineMapper subject;
//    private Transaction expected = JavaBeanGeneratorCreator
//            .of(Transaction.class).create();
//
//    /**
//     * id,transactionDetails,contact,value,timestamp
//     */
//    @Test
//    void mapLine() throws Exception {
//        int i  = 1;
//        subject = new TransactionLineMapper();
//
//        String csv = CsvWriter.toCSV(expected.id(),
//                expected.details(),
//                expected.contact(),
//                valueOf(expected.amount()),
//                valueOf(expected.timestamp()));
//
//        Transaction actual = subject.mapLine(csv,i);
//        assertEquals(expected, actual);
//    }
}