package com.technokratos.api.external;

import com.technokratos.dto.response.Judge0Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/judge0")
@Tag(name = "judge0 callback", description = "process judge0 execution results")
public interface Judge0Api {

    @Operation(
            summary = "handle callback from judge0",
            description = "processes judge0 result and updates internal state",
            requestBody = @RequestBody(
                    description = "judge0 execution result",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Judge0Response.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "callback processed successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request (null response)")
            }
    )
    @PutMapping("/webhook")
    ResponseEntity<Void> handleCallback(@RequestBody Judge0Response response);
}
