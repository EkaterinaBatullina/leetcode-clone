package com.technokratos.exception;

import org.springframework.http.HttpStatus;

public class EventSerializationException extends ServiceException {
    public EventSerializationException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
