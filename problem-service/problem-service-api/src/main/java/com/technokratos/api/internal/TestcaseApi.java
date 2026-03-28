package com.technokratos.api.internal;

import com.technokratos.dto.request.TestcaseRequest;
import com.technokratos.dto.response.TestcaseResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/internal/testcases")
@Tag(name = "testcase api", description = "internal endpoints for managing testcases")

@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")

public interface TestcaseApi {

    @Operation(
            summary = "get testcase by id",
            description = "retrieves a single testcase by its id",
            parameters = {
                    @Parameter(name = "id", description = "testcase id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "testcase found")
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    TestcaseResponse findById(@PathVariable("id") UUID id);

    @Operation(
            summary = "get all testcases for a problem",
            description = "retrieves all testcases associated with a specific problem",
            parameters = {
                    @Parameter(name = "problemId", description = "problem id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "list of testcases")
            }
    )
    @GetMapping("/problem/{problemId}")
    @ResponseStatus(HttpStatus.OK)
    List<TestcaseResponse> getAllByProblemId(@PathVariable("problemId")  UUID problemId);

    @Operation(
            summary = "delete testcase by id",
            description = "deletes a testcase by its id",
            parameters = {
                    @Parameter(name = "id", description = "testcase id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "testcase deleted")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") UUID id);

    @Operation(
            summary = "create new testcase",
            description = "creates a new testcase",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "testcase data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TestcaseRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "testcase created")
            }
    )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    UUID create(@RequestBody TestcaseRequest testcaseRequest);

    @Operation(
            summary = "replace testcase",
            description = "replaces an existing testcase with new data",
            parameters = {
                    @Parameter(name = "id", description = "testcase id", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "new testcase data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TestcaseRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "testcase replaced")
            }
    )
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void replace(@PathVariable("id") UUID id, @RequestBody TestcaseRequest testcaseRequest);

    @Operation(
            summary = "update testcase",
            description = "updates an existing testcase with partial data",
            parameters = {
                    @Parameter(name = "id", description = "testcase id", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "partial testcase data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TestcaseRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "testcase updated")
            }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void update(@PathVariable("id") UUID id, @RequestBody TestcaseRequest testcaseRequest);
}
