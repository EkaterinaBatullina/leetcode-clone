package com.technokratos.mapper;

import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "eventPayload.username", target = "username", defaultValue = "unknown")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    NotificationResponse toResponse(Notification notification);
}
