package com.technokratos.service.auth;

import com.nimbusds.jose.util.Pair;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.model.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ClientValidationService clientValidationService;
    private final UserValidationService userValidationService;
    private final TokenService tokenService;
    private final OAuth2AuthorizationSaver authorizationSaver;
    private final JwtDecoder jwtDecoder;

    public TokenCoupleResponse signIn(AuthenticationRequest request) {
        RegisteredClient client = clientValidationService.extractClientCredentials(SecurityContextHolder.getContext().getAuthentication());
        UserEntity user = userValidationService.validateUser(request);
        clientValidationService.checkGrantType(client, AuthorizationGrantType.JWT_BEARER);
        String tokenId = tokenService.generateTokenId(user);
        Pair<Jwt, Jwt> tokenPair = tokenService.createTokenPair(client, user, tokenId);
        authorizationSaver.save(client, AuthorizationGrantType.JWT_BEARER, tokenId, tokenPair, user.getUsername());
        return new TokenCoupleResponse(tokenPair.getLeft().getTokenValue(), tokenPair.getRight().getTokenValue());
    }

    public TokenCoupleResponse refreshTokens(@NotBlank String refreshToken) {
        Jwt verifiedToken = jwtDecoder.decode(refreshToken);
        RegisteredClient client = clientValidationService.getAndValidateRefreshClient();
        clientValidationService.checkGrantType(client, AuthorizationGrantType.REFRESH_TOKEN);
        UserEntity user = userValidationService.extractUserFromToken(verifiedToken);
        String tokenId = verifiedToken.getId();
        Pair<Jwt, Jwt> tokenPair = tokenService.createTokenPair(client, user, tokenId);
        authorizationSaver.save(client, AuthorizationGrantType.REFRESH_TOKEN, tokenId, tokenPair, user.getUsername());
        return new TokenCoupleResponse(tokenPair.getLeft().getTokenValue(), tokenPair.getRight().getTokenValue());
    }
}