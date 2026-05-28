package com.technokratos.service;

import com.nimbusds.jose.util.Pair;
import com.technokratos.config.property.SecurityProperties;
import com.technokratos.model.UserEntity;
import com.technokratos.dto.enums.Role;
import com.technokratos.service.auth.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.technokratos.util.SecurityConstant.AUTHORITIES;
import static com.technokratos.util.SecurityConstant.PROFILE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private SecurityProperties properties;

    @Test
    void generateTokenId() {
        UUID userId = UUID.randomUUID();
        String username = "username";
        UserEntity user = new UserEntity();
        user.setUuid(userId);
        user.setUsername(username);

        String tokenId = tokenService.generateTokenId(user);
        String expected = Base64.getEncoder().encodeToString((userId + "_" + username).getBytes());

        assertEquals(expected, tokenId);
    }

    @Test
    void createTokenPair() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUuid(userId);
        user.setUsername("username");
        user.setRole(Role.USER);

        RegisteredClient registeredClient = mock(RegisteredClient.class);
        TokenSettings tokenSettings = mock(TokenSettings.class);

        when(registeredClient.getScopes()).thenReturn(Set.of("scope1", "scope2"));
        when(registeredClient.getTokenSettings()).thenReturn(tokenSettings);
        when(tokenSettings.getAccessTokenTimeToLive()).thenReturn(Duration.ofHours(1));
        when(properties.getIssuer()).thenReturn("http://user-service:8080");

        Jwt accessJwt = mock(Jwt.class);
        Jwt refreshJwt = mock(Jwt.class);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(accessJwt)
                .thenReturn(refreshJwt);

        String tokenId = "token-id";

        Pair<Jwt, Jwt> tokens = tokenService.createTokenPair(registeredClient, user, tokenId);

        assertSame(accessJwt, tokens.getLeft());
        assertSame(refreshJwt, tokens.getRight());

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(captor.capture());

        List<JwtClaimsSet> allClaims = captor.getAllValues().stream()
                .map(JwtEncoderParameters::getClaims)
                .toList();

        JwtClaimsSet accessClaims = allClaims.get(0);
        JwtClaimsSet refreshClaims = allClaims.get(1);

        assertEquals(tokenId, accessClaims.getId());
        try {
            assertEquals(new URL("http://user-service:8080"), accessClaims.getIssuer());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        assertEquals(userId.toString(), accessClaims.getSubject());
        assertEquals(Set.of("scope1", "scope2"), accessClaims.getClaim(OAuth2ParameterNames.SCOPE));
        assertEquals("USER", accessClaims.getClaim(AUTHORITIES));
        assertEquals(OAuth2ParameterNames.ACCESS_TOKEN, accessClaims.getClaim(OAuth2ParameterNames.TOKEN_TYPE));

        assertNotNull(accessClaims.getExpiresAt());
        assertTrue(accessClaims.getExpiresAt().isAfter(Instant.now().minusSeconds(60)));

        assertEquals(tokenId, refreshClaims.getId());
        try {
            assertEquals(new URL("http://user-service:8080"), refreshClaims.getIssuer());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        assertNull(refreshClaims.getSubject());
        assertEquals(Set.of("scope1", "scope2"), refreshClaims.getClaim(OAuth2ParameterNames.SCOPE));
        assertEquals(userId, refreshClaims.getClaim(PROFILE_ID));
        assertEquals("USER", refreshClaims.getClaim(AUTHORITIES));
        assertEquals(OAuth2ParameterNames.REFRESH_TOKEN, refreshClaims.getClaim(OAuth2ParameterNames.TOKEN_TYPE));

        assertNotNull(refreshClaims.getExpiresAt());
        assertTrue(refreshClaims.getExpiresAt().isAfter(Instant.now().minusSeconds(60)));
    }

    @Test
    void createTokenPair_nullUser_throwsException() {
        RegisteredClient client = mock(RegisteredClient.class);
        assertThrows(NullPointerException.class, () -> tokenService.createTokenPair(client, null, "tokenId"));
    }
}