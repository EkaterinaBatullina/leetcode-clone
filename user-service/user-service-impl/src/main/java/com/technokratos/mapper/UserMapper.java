package com.technokratos.mapper;

import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.model.UserEntity;
import com.technokratos.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserFullRequest userFullRequest);

    UserResponse toResponse(UserEntity userEntity);
}