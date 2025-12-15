package com.technokratos.service.auth;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuth2AuthorizationSaver {
    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    public void save(RegisteredClient registeredClient, AuthorizationGrantType authorizationGrantType, String tokenId, Pair<Jwt, Jwt> tokenPair, String username) {
        Jwt accessToken = tokenPair.getLeft();
        Jwt refreshToken = tokenPair.getRight();
        Set<String> scopes = registeredClient.getScopes();
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(), scopes);
        OAuth2RefreshToken oAuth2RefreshToken
                = new OAuth2RefreshToken(refreshToken.getTokenValue(),
                refreshToken.getIssuedAt(), refreshToken.getExpiresAt());
        OAuth2Authorization oAuth2Authorization = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .id(tokenId)
                .accessToken(oAuth2AccessToken)
                .refreshToken(oAuth2RefreshToken)
                .authorizationGrantType(authorizationGrantType)
                .principalName(username)
                .build();
        oAuth2AuthorizationService.save(oAuth2Authorization);
    }
}