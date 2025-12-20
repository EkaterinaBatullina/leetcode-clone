package com.technokratos.api;

import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleAuthenticationRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management")
@RequestMapping("/api/v1/authentication")
public interface AuthenticationApi {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns access and refresh tokens",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenCoupleResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "User registration failed")
            }
    )
    @PostMapping("/register")
    @SecurityRequirement(name = "basicAuth")
    @ResponseStatus(HttpStatus.CREATED)
    TokenCoupleResponse register(@Valid @RequestBody UserFullRequest request);

    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns access and refresh tokens",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenCoupleResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Invalid credentials")
            }
    )
    @PostMapping("/login")
    @SecurityRequirement(name = "basicAuth")
    @ResponseStatus(HttpStatus.OK)
    TokenCoupleResponse login(@Valid @RequestBody AuthenticationRequest request);

    @Operation(
            summary = "Refresh token",
            description = "Generates a new token using a valid refresh token",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tokens refreshed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenCoupleResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Refresh token not found or invalid")
            }
    )
    @PostMapping("/token/refresh")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    TokenCoupleResponse refreshTokens(@RequestBody RefreshTokenRequest request);

    @Operation(
            summary = "Login via Google",
            description = "Authenticates a user using a Google OAuth2 token and returns access and refresh tokens",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful, returns access and refresh tokens",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenCoupleResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid Google token")
            }
    )
    @PostMapping("/login/google")
    @SecurityRequirement(name = "basicAuth")
    @ResponseStatus(HttpStatus.OK)
    TokenCoupleResponse loginWithGoogle(@Valid @RequestBody GoogleAuthenticationRequest request);
}