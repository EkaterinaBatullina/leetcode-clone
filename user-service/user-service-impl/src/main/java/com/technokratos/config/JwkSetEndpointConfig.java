package com.technokratos.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwkSetEndpointConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {

        /*
         * Формирование JWK (JSON Web Key) на основе RSA-пары ключей
         * для использования в JWT-подписи и валидации.
         *
         * Приватный ключ используется Authorization Server для подписи токенов,
         * публичный ключ публикуется через JWK Set endpoint и используется
         * resource-серверами для проверки подписи без доступа к секрету.
         *
         * Такой подход позволяет централизовать управление ключами
         * и отделить процесс подписи от верификации токенов.
         */
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }
}