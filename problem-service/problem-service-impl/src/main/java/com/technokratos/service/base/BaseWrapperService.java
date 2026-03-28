package com.technokratos.service.base;

import com.technokratos.dto.response.WrapperResponse;

import java.util.Optional;
import java.util.UUID;

public interface BaseWrapperService {
    Optional<WrapperResponse> findByProblemIdAndLanguageId(UUID problemId, int languageId);
}
