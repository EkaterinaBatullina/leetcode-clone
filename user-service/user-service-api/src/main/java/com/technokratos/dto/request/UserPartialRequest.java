package com.technokratos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPartialRequest(

    String username,

    @Email(message = "Email must be a valid address")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password
) {}