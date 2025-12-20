package com.technokratos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthenticationRequest(
    @NotBlank(message = "idToken must not be blank")
    String idToken
) {}
