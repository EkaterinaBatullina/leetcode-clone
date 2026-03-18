package com.technokratos.submissionserviceapi.api.internal;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.SubmissionResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("internal/submissions")
@Tag(name = "submission api", description = "manage code submissions")
public interface SubmissionApi {

    @Operation(
            summary = "get submission by id",
            description = "retrieves a single submission by its unique id (note that it's important for id to be string and not uuid here because otherwise the mongodb fails",
            responses = {
                    @ApiResponse(responseCode = "200", description = "submission found"),
                    @ApiResponse(responseCode = "404", description = "submission not found")
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    SubmissionResponse findById(@PathVariable("id") String id);

    @Operation(
            summary = "get all submissions by user",
            description = "retrieves all submissions associated with the given user id",
            parameters = {
                    @Parameter(name = "userId", description = "unique id of the user", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "list of submissions for the user")
            }
    )
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    List<SubmissionResponse> findAllByUserId(@RequestParam UUID userId);

    @Operation(
            summary = "delete submission by id",
            description = "removes a submission from the system using its id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "submission deleted"),
                    @ApiResponse(responseCode = "404", description = "submission not found")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") String id);

    @Operation(
            summary = "create new submission",
            description = "creates a new submission with provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "submission details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SubmissionRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "submission created successfully")
            }
    )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    String create(@RequestBody SubmissionRequest submissionRequest);

    @Operation(
            summary = "update existing submission",
            description = "applies partial update to an existing submission",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "fields to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SubmissionRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "submission updated"),
                    @ApiResponse(responseCode = "404", description = "submission not found")
            }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void update(@PathVariable("id") String id, @RequestBody SubmissionRequest submissionRequest);
}
