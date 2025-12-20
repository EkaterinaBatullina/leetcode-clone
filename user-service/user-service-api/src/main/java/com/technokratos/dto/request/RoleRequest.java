package com.technokratos.dto.request;

import com.technokratos.dto.enums.Role;
import com.technokratos.validation.annotation.EnumValue;

public record RoleRequest (

    @EnumValue(enumClass = Role.class)
    String role
) {}