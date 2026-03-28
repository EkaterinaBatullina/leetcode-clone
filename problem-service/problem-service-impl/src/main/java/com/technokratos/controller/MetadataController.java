package com.technokratos.controller;

import com.technokratos.api.internal.MetadataApi;
import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.dto.response.TagResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.service.MetadataService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MetadataController implements MetadataApi {
    private final MetadataService service;
    @Override
    public List<Difficulty> getDifficulties() {
        return service.getDifficulties();
    }

    @Override
    public List<TagResponse> getTags() {
        return service.getTags();
    }

    @Override
    public List<LanguageResponse> getLanguages() {
        return service.getLanguage();
    }
}
