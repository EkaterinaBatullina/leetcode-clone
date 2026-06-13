package com.technokratos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JwtTestUtil {
    @Autowired
    private JwtEncoder jwtEncoder;

    public String generateAdminToken() {
        return generateToken(UUID.randomUUID().toString(), "ADMIN");
    }

    public String generateUserToken() {
        return generateToken(UUID.randomUUID().toString(), "USER");
    }

    private String generateToken(String userId, String role) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://user-service:8080")
                .subject(userId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("authorities", role)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
