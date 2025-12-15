package com.technokratos.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.technokratos.util.SecurityConstant.TEST_CLIENT_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientValidationService {
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisteredClient extractClientCredentials(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getCredentials() == null) {
            throw new BadCredentialsException("Missing client credentials");
        }

        String clientId = authentication.getName();
        String clientSecret = authentication.getCredentials().toString();

        RegisteredClient registeredClient = Optional.ofNullable(registeredClientRepository.findByClientId(clientId))
                .orElseThrow(() -> new BadCredentialsException("Client not found"));

        if (!passwordEncoder.matches(clientSecret, registeredClient.getClientSecret())) {
            throw new BadCredentialsException("Invalid client secret");
        }

        return registeredClient;
    }

    public void checkGrantType(RegisteredClient registeredClient, AuthorizationGrantType authorizationGrantType) {
        if (registeredClient == null) {
            log.debug(" Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }
        boolean isGrantTypeValid = registeredClient.getAuthorizationGrantTypes().stream()
                .anyMatch(grantType -> grantType.getValue().equals(authorizationGrantType.getValue()));
        if (!isGrantTypeValid) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public RegisteredClient getAndValidateRefreshClient() {
        return Optional.ofNullable(registeredClientRepository.findByClientId(TEST_CLIENT_ID))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}