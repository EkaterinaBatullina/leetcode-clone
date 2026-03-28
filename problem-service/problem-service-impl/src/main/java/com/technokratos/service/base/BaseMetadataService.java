package com.technokratos.service.base;

import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.dto.response.TagResponse;
import com.technokratos.enums.Difficulty;

import java.util.List;

public interface BaseMetadataService {
    List<Difficulty> getDifficulties();

    List<TagResponse> getTags();

    List<LanguageResponse> getLanguage();
}
