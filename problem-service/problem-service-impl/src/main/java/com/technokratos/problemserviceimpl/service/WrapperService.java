package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
import com.technokratos.problemserviceimpl.mapper.WrapperMapper;
import com.technokratos.problemserviceimpl.repository.WrapperRepository;
import com.technokratos.problemserviceimpl.service.base.BaseWrapperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WrapperService implements BaseWrapperService {
    private final WrapperRepository repository;
    private final WrapperMapper mapper;

    @Override
    public Optional<WrapperResponse> findByProblemIdAndLanguageId(UUID problemId, int languageId) {
        return repository.findByProblemIdAndLanguageId(problemId, languageId)
                .map(mapper::toResponse);
    }

}
