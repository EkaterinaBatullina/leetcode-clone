package com.technokratos.problemserviceimpl.service.base;

import com.technokratos.problemserviceapi.dto.response.WrapperResponse;

import java.util.Optional;
import java.util.UUID;

public interface BaseWrapperService {
    Optional<WrapperResponse> findByProblemIdAndLanguageId(UUID problemId, int languageId);
}
