package com.technokratos.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionMetricsService {

    private final MeterRegistry meterRegistry;

    public <T> T timeSubmissionProcessing(UUID problemId, UUID submissionId, SubmissionProcessor<T> processor) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return processor.process();
        } finally {
            sample.stop(
                    Timer.builder("submission_processing_duration_seconds")
                            .description("time taken to process a submission")
                            .tag("problem_id", problemId.toString())
                            .register(meterRegistry)
            );
        }
    }

    @FunctionalInterface
    public interface SubmissionProcessor<T> {
        T process();
    }
}
