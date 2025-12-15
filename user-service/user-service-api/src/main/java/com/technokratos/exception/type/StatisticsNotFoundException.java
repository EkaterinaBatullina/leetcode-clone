package com.technokratos.exception.type;

import java.util.UUID;

public class StatisticsNotFoundException extends NotFoundServiceException {

    public StatisticsNotFoundException(UUID uuid) {
        super("User with id = '%s' - not found".formatted(uuid));
    }
}