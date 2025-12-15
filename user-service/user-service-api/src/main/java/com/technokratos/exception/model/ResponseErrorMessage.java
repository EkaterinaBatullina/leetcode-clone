package com.technokratos.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseErrorMessage {
    private List<Error> errors;

    @Data
    @AllArgsConstructor
    public static class Error {
        private String field;
        private String code;
        private String message;
    }
}