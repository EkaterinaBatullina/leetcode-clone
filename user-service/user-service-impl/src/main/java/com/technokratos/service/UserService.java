package com.technokratos.service;

import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse getById();

    UserResponse getByUsername(String username);

    Page<UserResponse> getAll(Pageable pageable);

    TokenCoupleResponse create(UserFullRequest userFullRequest);

    void delete();

    void update(UserFullRequest userFullRequest);

    void patch(UserPartialRequest userPartialRequest);

    void updateRole(UUID uuid, RoleRequest roleRequest);
}