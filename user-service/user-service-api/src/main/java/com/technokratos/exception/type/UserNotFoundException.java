package com.technokratos.exception.type;

import java.util.UUID;

public class UserNotFoundException extends NotFoundServiceException {

    public UserNotFoundException(UUID uuid) {
        super("User with id = '%s' - not found".formatted(uuid));
    }

    public UserNotFoundException(String username) {
        super("User with username = '%s' - not found".formatted(username));
    }
}