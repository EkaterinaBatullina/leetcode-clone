package com.technokratos.mapper;

import com.technokratos.dto.request.LanguageRequest;
import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.entity.Language;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageResponse toResponse(Language language);

    Language toEntity(LanguageRequest languageRequest);

    List<LanguageResponse> toResponse(List<Language> languages);

    List<Language> toEntity(List<LanguageRequest> languageRequest);
}
