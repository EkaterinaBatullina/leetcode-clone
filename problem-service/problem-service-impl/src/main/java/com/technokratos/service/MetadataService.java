package com.technokratos.service;

import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.dto.response.TagResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.mapper.LanguageMapper;
import com.technokratos.mapper.TagMapper;
import com.technokratos.repository.LanguageRepository;
import com.technokratos.repository.TagRepository;
import com.technokratos.service.base.BaseMetadataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MetadataService implements BaseMetadataService {
    private final TagRepository tagRepository;
    private final LanguageRepository languageRepository;
    private final TagMapper tagMapper;
    private final LanguageMapper languageMapper;
    @Override
    public List<Difficulty> getDifficulties() {
        return List.of(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD);
    }

    @Override
    public List<TagResponse> getTags() {
        return tagMapper.toResponse(tagRepository.findAll());
    }

    @Override
    public List<LanguageResponse> getLanguage() {
        return languageMapper.toResponse(languageRepository.findAll());
    }
}
