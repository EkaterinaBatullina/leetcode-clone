package com.technokratos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class PublishingFailedException extends ResponseStatusException {
    public PublishingFailedException(UUID id) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "publishing failed for problem with id: " + id);
    }
}
