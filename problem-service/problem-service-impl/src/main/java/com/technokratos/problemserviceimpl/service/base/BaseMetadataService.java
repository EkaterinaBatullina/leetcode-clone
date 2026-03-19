package com.technokratos.problemserviceimpl.service.base;

import com.technokratos.problemserviceapi.dto.response.LanguageResponse;
import com.technokratos.problemserviceapi.dto.response.TagResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;

import java.util.List;

public interface BaseMetadataService {
    List<Difficulty> getDifficulties();

    List<TagResponse> getTags();

    List<LanguageResponse> getLanguage();
}
