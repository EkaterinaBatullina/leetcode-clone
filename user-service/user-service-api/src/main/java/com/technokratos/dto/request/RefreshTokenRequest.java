package com.technokratos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest (

    @NotBlank(message = "Token must not be blank")
    String refreshToken
) {}