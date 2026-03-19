package com.technokratos.problemserviceimpl.controller;

import com.technokratos.problemserviceapi.api.internal.MetadataApi;
import com.technokratos.problemserviceapi.dto.response.LanguageResponse;
import com.technokratos.problemserviceapi.dto.response.TagResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceimpl.service.MetadataService;
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
