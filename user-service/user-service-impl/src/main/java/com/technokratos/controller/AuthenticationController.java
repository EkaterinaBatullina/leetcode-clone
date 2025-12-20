package com.technokratos.controller;

import com.technokratos.api.AuthenticationApi;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleAuthenticationRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.service.auth.AuthenticationService;
import com.technokratos.service.UserService;
import com.technokratos.service.auth.GoogleAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi {
    private final AuthenticationService authenticationService;
    private final GoogleAuthenticationService googleAuthenticationService;
    private final UserService userService;

    @Override
    public TokenCoupleResponse register(UserFullRequest request) {
        return userService.create(request);
    }

    @Override
    public TokenCoupleResponse login(AuthenticationRequest request) {
        return authenticationService.signIn(request);
    }

    @Override
    public TokenCoupleResponse refreshTokens(RefreshTokenRequest request) {
        return authenticationService.refreshTokens(request.refreshToken());
    }

    @Override
    public TokenCoupleResponse loginWithGoogle(GoogleAuthenticationRequest request) {
        return googleAuthenticationService.loginWithGoogle(request);
    }
}