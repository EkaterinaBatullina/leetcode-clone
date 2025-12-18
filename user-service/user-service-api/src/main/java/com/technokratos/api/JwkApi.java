package com.technokratos.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Tag(name = "JWK", description = "Endpoints for JSON Web Key Sets")
@RequestMapping("/.well-known")
public interface JwkApi {

    @Operation(
            summary = "Get JSON Web Key Set",
            description = "Returns the JWKS (JSON Web Key Set) containing public keys for token verification",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWKS retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "JWKS not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/jwks.json")
    Map<String, Object> keys();
}