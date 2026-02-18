package com.technokratos.service.auth;

import com.nimbusds.jose.util.Pair;
import com.technokratos.config.property.SecurityProperties;
import com.technokratos.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static com.technokratos.util.SecurityConstant.*;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final SecurityProperties properties;

    public String generateTokenId(UserEntity user) {
        return Base64.getEncoder().encodeToString((user.getUuid() + "_" + user.getUsername()).getBytes());
    }

    public Pair<Jwt, Jwt> createTokenPair(RegisteredClient registeredClient, UserEntity userEntity, String tokenId) {
        Jwt accessToken = createAccessToken(registeredClient, userEntity, tokenId);
        Jwt refreshToken = createRefreshToken(registeredClient,userEntity, tokenId);
        return Pair.of(accessToken, refreshToken);
    }

    private Jwt createAccessToken(final RegisteredClient registeredClient, final UserEntity userEntity, final String tokenId) {
        UUID profileId = userEntity.getUuid();
        String role = userEntity.getRole().name();
        long accessTokenTtl = registeredClient.getTokenSettings().getAccessTokenTimeToLive().toMillis();
        Set<String> scopes = registeredClient.getScopes();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(tokenId)
                .issuer(properties.getIssuer())
                .subject(String.valueOf(profileId))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(accessTokenTtl))
                .claim(OAuth2ParameterNames.SCOPE, scopes)
                .claim(AUTHORITIES, role)
                .claim(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.ACCESS_TOKEN)
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JWS_HEADER, jwtClaimsSet);
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    private Jwt createRefreshToken(final RegisteredClient registeredClient, final UserEntity userEntity, final String tokenId) {
        UUID profileId = userEntity.getUuid();
        String role = userEntity.getRole().name();
        Set<String> scopes = registeredClient.getScopes();
        long refreshTokenTtl = registeredClient.getTokenSettings().getRefreshTokenTimeToLive().toMillis();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(tokenId)
                .issuer(properties.getIssuer())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(refreshTokenTtl))
                .claim(OAuth2ParameterNames.SCOPE, scopes)
                .claim(PROFILE_ID, profileId)
                .claim(AUTHORITIES, role)
                .claim(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.REFRESH_TOKEN)
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JWS_HEADER, jwtClaimsSet);
        return jwtEncoder.encode(jwtEncoderParameters);
    }
}