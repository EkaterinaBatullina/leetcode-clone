package com.technokratos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class TestcaseNotFoundException extends ResponseStatusException {
    public TestcaseNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, "testcase not found with ID: " + id);
    }
}
