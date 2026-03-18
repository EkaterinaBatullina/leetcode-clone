package com.technokratos.problemserviceapi.api.external;


import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/problems")
@Tag(name = "external problem api", description = "public endpoints for accessing problem data")
public interface ExternalProblemApi {

    @Operation(

            summary = "run problem code",
            description = "submits a run request to judge0 for testing a solution against testcases",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "run request details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RunRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "run initiated")
            }
    )
    @PostMapping("/run")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @SecurityRequirement(name = "bearerAuth")
    void run(@RequestBody RunRequest request);

    @Operation(
            summary = "submit solution for evaluation",
            description = "submits a solution for full evaluation and scoring",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "submit request details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RunRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "submission accepted")
            }
    )
    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @SecurityRequirement(name = "bearerAuth")
    void submit(@RequestBody RunRequest request);

    @Operation(
            summary = "get problem by id",
            description = "retrieves a problem using its unique identifier",
            parameters = {
                    @Parameter(name = "id", description = "unique id of the problem", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "problem found"),
                    @ApiResponse(responseCode = "404", description = "problem not found")
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ProblemResponse findById(@PathVariable("id") UUID id);

    @Operation(
            summary = "get all problems",
            description = "returns a list of all available problems",
            responses = {
                    @ApiResponse(responseCode = "200", description = "list of problems")
            }
    )
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    List<ProblemResponse> getAll();

    @Operation(
            summary = "filter and paginate problems",
            description = "returns a paginated list of problems filtered by difficulty, category, and tag",
            parameters = {
                    @Parameter(name = "difficulty", description = "optional list of difficulty levels"),

                    @Parameter(name = "tag", description = "optional list of tags"),
                    @Parameter(name = "page", description = "page number for pagination"),
                    @Parameter(name = "size", description = "number of items per page"),
                    @Parameter(name = "sort", description = "sorting criteria")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "paginated list of problems")
            }
    )
    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    Page<ProblemResponse> getAllWithPagination(
            @RequestParam(required = false) List<Difficulty> difficulty,
            @RequestParam(required = false) List<String> tag,
            Pageable pageable
    );
}

