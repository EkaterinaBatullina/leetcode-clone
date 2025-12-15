package com.technokratos.api;

import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/authentication")
public interface AuthenticationApi {

    @PostMapping("/register")
    @SecurityRequirement(name = "basicAuth")
    @ResponseStatus(HttpStatus.CREATED)
    TokenCoupleResponse register(@Valid @RequestBody UserFullRequest request);

    @PostMapping("/login")
    @SecurityRequirement(name = "basicAuth")
    @ResponseStatus(HttpStatus.OK)
    TokenCoupleResponse login(@Valid @RequestBody AuthenticationRequest request);

    @PostMapping("/token/refresh")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    TokenCoupleResponse refreshTokens(@RequestBody RefreshTokenRequest request);
}