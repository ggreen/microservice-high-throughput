package showcase.high.throughput.microservices.benchmark.kafka;

import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.operations.performance.stats.ThroughputStatistics;
import nyla.solutions.core.util.Text;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Properties;

@Component
public class KafkaBenchMarkRunner implements ApplicationRunner {
    @Value("${bench.mark.loopCount:2000000}")
    private Long loopCount;

    @Value("${bench.mark.threadCount:10}")
    private int threadCount;
    private String key = "";
    @Value("${bench.mark.lifeTimeSecs:60}")
    private Long lifeTimeSecs;
    private String value = Text.generateAlphabeticId(1000);

    @Value("${bench.mark.statsCapacity:100}")
    private int statsCapacity;
    private ThroughputStatistics throughtput = new ThroughputStatistics();

    @Override
    public void run(ApplicationArguments args) throws Exception {

       var benchMark=  BenchMarker.builder()
               .loopCount(loopCount)
               .threadCount(threadCount)
               .threadLifeTimeSeconds(lifeTimeSecs)
               .build();

        var props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        var producer = new KafkaProducer<String, String>(props);

        PerformanceCheck perfTest = new PerformanceCheck(benchMark,statsCapacity);
        var start = LocalDateTime.now();
        LocalDateTime end = null;
        perfTest.perfCheck(()->{
                        producer.send(new ProducerRecord<String, String>("benchmark", key,value));
                    });
        end = LocalDateTime.now();

        throughtput.increment(loopCount);

        var tps = throughtput.throughputPerSecond(start,end);

        System.out.println(perfTest.getReport());
        System.out.println("tps:"+tps);
        System.out.flush();

    }
}
