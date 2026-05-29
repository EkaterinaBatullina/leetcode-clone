package com.technokratos.config;

import com.technokratos.config.properties.KeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.interfaces.RSAPublicKey;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {
    private final KeyProperties properties;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String cleanKey = properties.getPublicKey().replace("\n", "").replace("\r", "").trim();
        byte[] decoded = java.util.Base64.getDecoder().decode(cleanKey);
        java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decoded);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        return (java.security.interfaces.RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    @Bean
    public JwtDecoder jwtDecoder(java.security.interfaces.RSAPublicKey rsaPublicKey) {
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}