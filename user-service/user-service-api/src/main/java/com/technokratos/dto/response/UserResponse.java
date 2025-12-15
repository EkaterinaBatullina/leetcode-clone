package com.technokratos.dto.response;

import java.util.UUID;

public record UserResponse(UUID uuid, String username, String email, String role) {}