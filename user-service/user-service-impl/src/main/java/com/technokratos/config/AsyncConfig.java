package com.technokratos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    /*
     * Пул предназначен для асинхронного обновления статусов Outbox-событий.
     *
     * Kafka callback threads не выполняют операции с БД напрямую,
     * что предотвращает распространение проблем БД на процесс
     * публикации сообщений и сохраняет стабильность producer-а
     * при временной перегрузке системы.
     */
    @Bean
    public Executor outboxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("OutboxWorker-");

        executor.initialize();
        return executor;
    }
}
