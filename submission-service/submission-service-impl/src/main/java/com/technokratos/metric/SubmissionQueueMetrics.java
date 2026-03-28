package com.technokratos.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionQueueMetrics {

    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void registerGauge() {
        Gauge.builder("submission_queue_size", redisTemplate, r -> {
                    Long size = r.opsForList().size("submission:queue");
                    return size != null ? size : 0.0;
                })
                .description("number of submissions waiting to be processed in redis")
                .register(meterRegistry);
    }
}

