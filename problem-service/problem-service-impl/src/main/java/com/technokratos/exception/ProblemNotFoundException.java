package com.technokratos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class ProblemNotFoundException extends ResponseStatusException {
    public ProblemNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, "problem not found with ID: " + id);
    }
}
