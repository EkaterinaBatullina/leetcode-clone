package com.technokratos.api;

import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "Operations related to user management")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(
            summary = "Get current user info",
            description = "Returns detailed information about the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User info retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    UserResponse getMe();

    @Operation(
            summary = "Get user statistics",
            description = "Returns solved tasks count, attempts, and success percentages for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statistics retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StatisticResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "User statistics not found")
            }
    )
    @GetMapping("/me/statistic")
    @ResponseStatus(HttpStatus.OK)
    StatisticResponse getStatistic();

    @Operation(
            summary = "Get user by username",
            description = "Returns the information of a user specified by their username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    UserResponse getByUsername(@PathVariable("username") String username);

    @Operation(
            summary = "Get all users with pagination",
            description = "Returns a paginated list of users. Only accessible by ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of users retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No users found")
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Page<UserResponse>> getAll(Pageable pageable);

    @Operation(
            summary = "Update current user",
            description = "Updates the profile of the authenticated user.",
            requestBody = @RequestBody(
                    description = "User data to update",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserFullRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/me")
    ResponseEntity<Void> updateMe(@Valid @RequestBody UserFullRequest userFullRequest);

    @Operation(
            summary = "Delete current user",
            description = "Deletes the authenticated user account.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @DeleteMapping("/me")
    ResponseEntity<Void> delete();

    @Operation(
            summary = "Update current user partially",
            description = "Updates only the provided fields of the authenticated user.",
            requestBody = @RequestBody(
                    description = "Partial user data to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserPartialRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PatchMapping("/me")
    ResponseEntity<Void> patch(@Valid @RequestBody UserPartialRequest userPartialRequest);

    @Operation(
            summary = "Update user role",
            description = "Updates the role of a user with the given ID. Only accessible by ADMIN.",
            requestBody = @RequestBody(
                    description = "Role to assign",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RoleRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden, requires ADMIN role")
            }
    )
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> updateRole(@PathVariable("id") UUID uuid, @Valid @RequestBody RoleRequest roleRequest);
}