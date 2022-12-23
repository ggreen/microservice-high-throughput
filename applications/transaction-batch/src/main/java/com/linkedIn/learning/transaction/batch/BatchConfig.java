//package com.linkedIn.learning.transaction.batch;
//
//import nyla.solutions.core.io.csv.CsvReader;
//import nyla.solutions.core.patterns.batch.BatchJob;
//import nyla.solutions.core.patterns.batch.BatchReport;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URI;
//import java.nio.file.Paths;
//import java.util.Iterator;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//@Configuration
//public class BatchConfig {
//
//    private int batchChunkSize = 1000;
//    private String inputFilePath;
//
//    Supplier<List<String>> rowSupplier(Iterator<List<String>> rowIterator) throws IOException {
//        File file = Paths.get(inputFilePath).toFile();
//        return () -> {
//            if (rowIterator.hasNext())
//                return rowIterator.next();
//            else
//                return null;
//        };
//    }
//
//
//
//    @Bean
//    BatchJob<List<String>,List<String>> batchJob(Supplier<List<String>> supplier,
//                                                 Consumer<List<String>> consumer, Function processor)
//    {
//        return BatchJob.builder().supplier(supplier)
//                .consumer(consumer)
//                .batchChunkSize(batchChunkSize).processor(
//                        processor).build();
//    }
//
//    ApplicationRunner runner(BatchJob<List<String>,List<String>> batchJob){
//        return (args) -> {
//            BatchReport batchRecord = batchJob.execute();
//            System.out.println(batchRecord);
//        };
//    }
//}
