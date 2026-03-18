package com.technokratos.problemserviceimpl.mapper;

import com.technokratos.problemserviceapi.dto.request.LanguageRequest;
import com.technokratos.problemserviceapi.dto.response.LanguageResponse;
import com.technokratos.problemserviceimpl.entity.Language;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageResponse toResponse(Language language);

    Language toEntity(LanguageRequest languageRequest);

    List<LanguageResponse> toResponse(List<Language> languages);

    List<Language> toEntity(List<LanguageRequest> languageRequest);
}
