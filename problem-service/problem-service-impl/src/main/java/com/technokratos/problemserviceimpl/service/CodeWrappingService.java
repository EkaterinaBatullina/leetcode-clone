package com.technokratos.problemserviceimpl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeWrappingService {

    private static final String PLACEHOLDER = "__USER_CODE__";

    public String wrapUserCode(String userCode, String wrapperTemplate) {
        if (!wrapperTemplate.contains(PLACEHOLDER)) {
            throw new IllegalArgumentException("wrapper does not contain user code placeholder.");
        }
        return wrapperTemplate.replace(PLACEHOLDER, userCode);
    }
}
