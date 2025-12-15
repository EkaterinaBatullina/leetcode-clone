package com.technokratos.config;

import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.service.auth.RegisteredClientService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2ClientInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final RegisteredClientService registeredClientService;
    private final OAuth2ClientProperties oAuth2ClientProperties;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        registeredClientService.create(oAuth2ClientProperties.getClientId(), oAuth2ClientProperties.getClientSecret());
    }
}