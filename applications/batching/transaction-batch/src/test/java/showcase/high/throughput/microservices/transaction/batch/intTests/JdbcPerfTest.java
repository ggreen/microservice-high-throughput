package showcase.high.throughput.microservices.transaction.batch.intTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import showcase.high.throughput.microservices.domain.Payment;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.jdbc.Sql;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.valueOf;

public class JdbcPerfTest {

        @Test
        @EnabledIfSystemProperty(named = "intTest", matches = "true")
        void jdbcPerfTest() {

            Sql sql = new Sql();

            String driver = Driver.class.getName();
            String connectionUrl = "jdbc:postgresql://localhost/postgres";
            String username = "postgres";
            char[] password = null;


            ObjectMapper objectMapper = new ObjectMapper();

            final int[] i = {1};
            int expectedCount = 2000000;

            Supplier<Payment> supplier = () -> {
                if(i[0] > expectedCount)
                    return null;
                i[0]++;
                return new Payment(valueOf(i[0]),valueOf(i[0]),null,null,0,null);

            };

            int batchChunkSize = 10000;

            try(Connection connection = Sql.createConnection(driver,connectionUrl,username,password))
            {
                sql.execute(connection,"truncate table ms_transactions");

                try(PreparedStatement preparedStatement = connection.prepareStatement("insert into ms_transactions(id,details) values (?,?)"))
                {
                    Consumer<List<Payment>> consumer = transactionList -> {
                        transactionList.forEach(transaction ->
                        {
                            try {
                                preparedStatement.setString(1,transaction.id());
                                preparedStatement.setString(2,transaction.details());

                                preparedStatement.addBatch();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        );

                        try {
                            preparedStatement.executeBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    };

                    BatchJob batchJob = BatchJob.builder().supplier(supplier)
                            .consumer(consumer)
                            .batchChunkSize(batchChunkSize)
                            .build();

                    BatchReport batchRecord = batchJob.execute();

                    System.out.println(batchRecord+" throughput per milliseconds:"+batchRecord.transactionsPerMs());

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
}
