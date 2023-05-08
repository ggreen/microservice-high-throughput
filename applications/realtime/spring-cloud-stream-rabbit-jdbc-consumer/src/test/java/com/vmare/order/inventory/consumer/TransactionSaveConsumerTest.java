package com.vmare.order.inventory.consumer;

import showcase.high.throughput.microservices.domain.Transaction;
import com.vmare.order.inventory.repository.TransactionJdbcRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionSaveConsumerTest {

      @Mock
      private TransactionJdbcRepository repository;

      @Mock
      private TransactionSaveConsumer subject;
      private Transaction expected = JavaBeanGeneratorCreator.of(Transaction.class).create();

      @Test
      void given_TransactionJdbcRepository_when_save_then_when_read_you_have_TransactionJdbcRepository_returned() {

          subject = new TransactionSaveConsumer(repository);

          subject.accept(expected);

          verify(this.repository).save(any(Transaction.class));
      }
}