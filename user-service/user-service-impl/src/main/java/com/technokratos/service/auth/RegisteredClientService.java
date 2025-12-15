package com.technokratos.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisteredClientService {
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(String clientId, String clientSecret) {
        Optional<RegisteredClient> existingClient = Optional.ofNullable(registeredClientRepository.findByClientId(clientId));
        if (existingClient.isEmpty()) {
            UUID newClientId = UUID.randomUUID();
            RegisteredClient registeredClient = RegisteredClient.withId(String.valueOf(newClientId))
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode(clientSecret))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantTypes(grantTypes -> {
                        grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                        grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                        grantTypes.add(AuthorizationGrantType.JWT_BEARER);
                    })
                    .scopes(scopes -> {
                        scopes.add("read");
                        scopes.add("write");
                    })
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(1))
                            .refreshTokenTimeToLive(Duration.ofDays(1))
                            .build())
                    .build();
            registeredClientRepository.save(registeredClient);
        } else {
            log.info("Client already exists: {}", clientId);
        }
    }
}