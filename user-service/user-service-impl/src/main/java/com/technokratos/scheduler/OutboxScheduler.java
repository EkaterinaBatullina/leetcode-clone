package com.technokratos.scheduler;

import com.technokratos.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        outboxService.processOutbox();
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStuckEvents() {
        outboxService.recoverStuckEvents();
    }
}
