package com.technokratos.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.technokratos.config.property.GoogleClientProperties;
import com.technokratos.dto.request.GoogleAuthenticationRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthenticationService {
    private final TokenService tokenService;
    private final UserValidationService userValidationService;
    private final OAuth2AuthorizationSaver authorizationSaver;
    private final ClientValidationService clientValidationService;
    private final GoogleClientProperties properties;
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    public TokenCoupleResponse loginWithGoogle(GoogleAuthenticationRequest request) {
        try {
            GoogleIdToken.Payload payload = verifyGoogleToken(request.idToken());
            UserEntity user = getOrCreateUser(payload);
            RegisteredClient client = clientValidationService.getAndValidateRefreshClient();
            return generateTokensAndSaveAuthorization(user, client);
        } catch (Exception e) {
            log.warn("Failed to login with Google token", e);
            throw new BadCredentialsException("Invalid Google ID token");
        }
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            log.info("Starting verification of Google ID token.");
            log.info("Incoming ID Token: {}", idToken);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory
            )
                    .setAudience(Collections.singletonList(properties.getClientId()))
                    .build();

            log.info("Verifier created with audience: {}", properties.getClientId());

            GoogleIdToken token = verifier.verify(idToken);

            if (token == null) {
                log.error("GoogleIdTokenVerifier returned null! Token could not be verified.");
                throw new BadCredentialsException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = token.getPayload();

            return payload;
        } catch (Exception e) {
            log.error("Exception occurred while verifying Google ID token", e);
            throw new BadCredentialsException("Invalid Google ID token", e);
        }
    }

    private UserEntity getOrCreateUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String username = (String) payload.get("name");

        return userValidationService.getOrCreateUserFromGoogle(email, username);
    }

    private TokenCoupleResponse generateTokensAndSaveAuthorization(UserEntity user, RegisteredClient client) {
        String tokenId = tokenService.generateTokenId(user);
        var tokenPair = tokenService.createTokenPair(client, user, tokenId);

        authorizationSaver.save(client, AuthorizationGrantType.AUTHORIZATION_CODE, tokenId, tokenPair, user.getUsername());

        return new TokenCoupleResponse(
                tokenPair.getLeft().getTokenValue(),
                tokenPair.getRight().getTokenValue()
        );
    }
}
