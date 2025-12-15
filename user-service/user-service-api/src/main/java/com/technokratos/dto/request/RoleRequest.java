package com.technokratos.dto.request;

import com.technokratos.dto.enums.Role;
import com.technokratos.validation.EnumValue;

public record RoleRequest (

    @EnumValue(enumClass = Role.class)
    String role
) {}