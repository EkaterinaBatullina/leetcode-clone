package com.technokratos.exception;

import com.technokratos.exception.NotFoundServiceException;

import java.util.UUID;

public class StatisticsNotFoundException extends NotFoundServiceException {

    public StatisticsNotFoundException(UUID uuid) {
        super("User with id = '%s' - not found".formatted(uuid));
    }
}