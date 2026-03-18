package com.technokratos.problemserviceapi.api.internal;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/internal/problems")
@Tag(name = "problem api", description = "internal endpoints for managing problems and triggering executions")

@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public interface ProblemApi {

    @Operation(
            summary = "delete problem by id",
            description = "deletes a problem entity by its unique identifier",
            parameters = {
                    @Parameter(name = "id", description = "problem id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "problem deleted")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") UUID id);

    @Operation(
            summary = "create new problem",
            description = "creates a new problem and returns its id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "problem data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProblemRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "problem created")
            }
    )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    UUID create(@RequestBody ProblemRequest userRequest);

    @Operation(
            summary = "replace existing problem",
            description = "replaces the entire problem with new data",
            parameters = {
                    @Parameter(name = "id", description = "problem id", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "new problem data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProblemRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "problem replaced")
            }
    )
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void replace(@PathVariable("id") UUID id, @RequestBody ProblemRequest userRequest);

    @Operation(
            summary = "update problem",
            description = "updates an existing problem partially",
            parameters = {
                    @Parameter(name = "id", description = "problem id", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "partial problem data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProblemRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "problem updated")
            }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void update(@PathVariable("id") UUID id, @RequestBody ProblemRequest userRequest);

    @Operation(
            summary = "publish problems",
            description = "publishes all problems to kafka for synchronization",
            responses = {
                    @ApiResponse(responseCode = "202", description = "problems published")
            }
    )
    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void publishProblems();

    @Operation(
            summary = "publish testcases",
            description = "publishes all testcases for a given problem to kafka",
            parameters = {
                    @Parameter(name = "problemId", description = "problem id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "testcases published")
            }
    )
    @PostMapping("/publish/{id}")
    @ResponseStatus(HttpStatus.OK)
    void publishTestcases(@PathVariable("id") UUID id);
}

