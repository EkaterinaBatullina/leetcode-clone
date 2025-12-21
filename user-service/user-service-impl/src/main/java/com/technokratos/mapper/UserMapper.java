package com.technokratos.mapper;

import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.model.UserEntity;
import com.technokratos.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserFullRequest userFullRequest);

    @Mapping(source = "uuid", target = "uuid")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    UserResponse toResponse(UserEntity userEntity);
}