package com.technokratos.api;

import com.technokratos.dto.enams.Status;
import com.technokratos.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(
        name = "Notification",
        description = "Endpoints for viewing user notifications and their delivery status"
)
@RequestMapping("/api/v1/notifications")
public interface NotificationApi {

    @Operation(
            summary = "Get notifications by status",
            description = "Returns a paginated list of notifications filtered by delivery status",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notifications successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid status value"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    )
            }
    )
    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    Page<NotificationResponse> getAllByStatus(@PathVariable("status") Status status,
                                              @PageableDefault(size = 10, page = 0) Pageable pageable);

    @Operation(
            summary = "Get notifications by user Id",
            description = "Returns a paginated list of notifications for a specific user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notifications successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid userId format"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    )
            }
    )
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    Page<NotificationResponse> getAllByUserId(@PathVariable("userId") UUID userId,
                                              @PageableDefault(size = 10, page = 0) Pageable pageable);
}
