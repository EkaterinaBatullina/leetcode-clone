package com.technokratos.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class NotificationMetricsService {
    private final Counter notificationSentCounter;
    private final Counter notificationFailedCounter;
    private final Timer notificationSendTimer;
    private final MeterRegistry registry;

    public NotificationMetricsService(MeterRegistry meterRegistry) {
        notificationSentCounter = meterRegistry.counter("notification.sent");
        notificationFailedCounter = meterRegistry.counter("notification.failed");
        notificationSendTimer = meterRegistry.timer("notification.send.duration");
        registry = meterRegistry;
    }

    public void incrementSent() {
        notificationSentCounter.increment();
    }

    public void incrementFailed() {
        notificationFailedCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(notificationSendTimer);
    }
}
