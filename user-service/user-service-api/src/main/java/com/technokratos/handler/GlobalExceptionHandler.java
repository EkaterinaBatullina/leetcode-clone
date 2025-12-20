package com.technokratos.handler;

import com.technokratos.dto.ResponseErrorMessage;
import com.technokratos.exception.NotFoundServiceException;
import com.technokratos.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ResponseErrorMessage> handLeMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorMessage.builder()
                        .errors(
                                ex.getBindingResult().getFieldErrors().stream()
                                        .map(fieldError -> new ResponseErrorMessage.Error(
                                                fieldError.getField(),
                                                fieldError.getCode(),
                                                fieldError.getDefaultMessage()))
                                        .collect(Collectors.toList()))
                        .build());
    }

    @ExceptionHandler(NotFoundServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}