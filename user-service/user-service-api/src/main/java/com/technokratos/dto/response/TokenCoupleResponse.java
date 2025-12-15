package com.technokratos.dto.response;

public record TokenCoupleResponse (
    String accessToken,
    String refreshToken
) {}