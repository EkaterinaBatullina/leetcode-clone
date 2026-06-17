package com.technokratos.controller;

import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.service.StatisticService;
import com.technokratos.service.UserService;
import lombok.RequiredArgsConstructor;
import com.technokratos.api.UserApi;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final StatisticService statisticService;

    @Override
    public UserResponse getMe() {
        return userService.getById();
    }

    @Override
    public StatisticResponse getStatistic() {
        return statisticService.getById();
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userService.getByUsername(username);
    }

    @Override
    public ResponseEntity<Page<UserResponse>> getAll(
            @PageableDefault(size = 10, page = 0, sort = "username") Pageable pageable) {
        Page<UserResponse> response = userService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> updateMe(UserFullRequest userFullRequest) {
        userService.update(userFullRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete() {
        userService.delete();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> patch(UserPartialRequest userPartialRequest) {
        userService.patch(userPartialRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateRole(UUID uuid, RoleRequest roleRequest) {
        userService.updateRole(uuid, roleRequest);
        return ResponseEntity.noContent().build();
    }
}