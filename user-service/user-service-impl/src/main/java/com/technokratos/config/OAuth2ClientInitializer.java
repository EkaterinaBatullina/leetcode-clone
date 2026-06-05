package com.technokratos.config;

import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.service.auth.RegisteredClientService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/*
 * Автоматическая регистрация тестового OAuth2-клиента при старте приложения.
 *
 * Используется для локальной разработки и тестового окружения:
 * клиент автоматически создаётся в БД и может использоваться для получения JWT
 * (например через Swagger).
 *
 * В production окружении клиенты должны быть созданы явно
 * через административные процессы или Identity Provider.
 */
@Component
@RequiredArgsConstructor
public class OAuth2ClientInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final RegisteredClientService registeredClientService;
    private final OAuth2ClientProperties oAuth2ClientProperties;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        registeredClientService.create(oAuth2ClientProperties.getClientId(),
                oAuth2ClientProperties.getClientSecret());
    }
}