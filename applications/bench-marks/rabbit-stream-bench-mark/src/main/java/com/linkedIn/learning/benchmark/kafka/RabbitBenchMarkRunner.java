package com.linkedIn.learning.benchmark.kafka;

import com.rabbitmq.stream.Environment;
import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.operations.performance.PerformanceCheck;
import nyla.solutions.core.operations.performance.stats.ThroughputStatistics;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class RabbitBenchMarkRunner implements ApplicationRunner
{
    @Value("${bench.mark.loopCount:2000000}")
    private Long loopCount;

    @Value("${bench.mark.threadCount:10}")
    private int threadCount;
    private String key = "";
    @Value("${bench.mark.lifeTimeSecs:60}")
    private Long lifeTimeSecs;
    private byte[] value = Text.generateAlphabeticId(1000).getBytes(StandardCharsets.UTF_8);

    @Value("${bench.mark.statsCapacity:100}")
    private int statsCapacity;
    private ThroughputStatistics throughtput = new ThroughputStatistics();
    private String streamName = "benchMark";

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var benchMark=  BenchMarker.builder()
                .loopCount(loopCount)
                .threadCount(threadCount)
                .threadLifeTimeSeconds(lifeTimeSecs)
                .build();

        var env = Environment.builder().build();
        env.streamCreator().stream(streamName).create();

        try{ env.deleteStream(streamName); } catch (Exception e){}
        env.streamCreator().stream(streamName).create();


        var producer = env.producerBuilder().stream(streamName)
                //.batchSize(batchSize)
                .build();

        PerformanceCheck perfTest = new PerformanceCheck(benchMark,statsCapacity);
        var start = LocalDateTime.now();
        LocalDateTime end = null;
        perfTest.perfCheck(()->{
            producer.send(producer.messageBuilder().addData(value).build(),
                    null);
        });
        end = LocalDateTime.now();

        throughtput.increment(loopCount);

        var tps = throughtput.throughputPerSecond(start,end);

        System.out.println(perfTest.getReport());
        System.out.println("tps:"+tps);
        System.out.flush();

    }
}
