package com.technokratos.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.JwsHeader;

@UtilityClass
public class SecurityConstant {
    public static final String AUTHORITIES = "authorities";
    public static final String PROFILE_ID = "profileId";
    public static final String TEST_CLIENT_ID = "my-client-id";
    public static final JwsHeader JWS_HEADER = JwsHeader.with(() -> "RS256").build();
}