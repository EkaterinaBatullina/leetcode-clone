package com.technokratos.api;

import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    UserResponse getMe();

    @GetMapping("/me/statistic")
    @ResponseStatus(HttpStatus.OK)
    StatisticResponse getStatistic();

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    UserResponse getByUsername(@PathVariable("username") String username);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Page<UserResponse>> getAll(Pageable pageable);

    @PutMapping("/me")
    ResponseEntity<Void> updateMe(@Valid @RequestBody UserFullRequest userFullRequest);

    @DeleteMapping("/me")
    ResponseEntity<Void> delete();

    @PatchMapping("/me")
    ResponseEntity<Void> patch(@Valid @RequestBody UserPartialRequest userPartialRequest);

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> updateRole(@PathVariable("id") UUID uuid, @Valid @RequestBody RoleRequest roleRequest);
}