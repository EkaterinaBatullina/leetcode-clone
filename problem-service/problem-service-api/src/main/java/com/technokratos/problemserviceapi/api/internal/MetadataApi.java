package com.technokratos.problemserviceapi.api.internal;


import com.technokratos.problemserviceapi.dto.response.LanguageResponse;
import com.technokratos.problemserviceapi.dto.response.TagResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/internal/metadata")
@Tag(name = "metadata api", description = "internal endpoints for retrieving metadata like difficulties and tags")

@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public interface MetadataApi {

    @GetMapping("/difficulties")
    @ResponseStatus(HttpStatus.OK)

    @Operation(
            summary = "get all difficulties",
            description = "retrieves a list of all available difficulty levels",
            responses = {

                    @ApiResponse(responseCode = "200", description = "list of difficulties"),
                    @ApiResponse(responseCode = "401", description = "unauthorized"),
                    @ApiResponse(responseCode = "403", description = "forbidden")
            }
    )
    List<Difficulty> getDifficulties();

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)

    @Operation(
            summary = "get all tags",
            description = "retrieves a list of all available tags",
            responses = {

                    @ApiResponse(responseCode = "200", description = "list of tags"),
                    @ApiResponse(responseCode = "401", description = "unauthorized"),
                    @ApiResponse(responseCode = "403", description = "forbidden")
            }
    )
    List<TagResponse> getTags();

    @GetMapping("/languages")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get all languages",
            description = "retrieves a list of all available languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "list of languages"),
                    @ApiResponse(responseCode = "401", description = "unauthorized"),
                    @ApiResponse(responseCode = "403", description = "forbidden")
            }
    )
    List<LanguageResponse> getLanguages();
}
