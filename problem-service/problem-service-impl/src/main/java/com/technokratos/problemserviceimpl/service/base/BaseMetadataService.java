package com.technokratos.problemserviceimpl.service.base;

<<<<<<< HEAD
=======
import com.technokratos.problemserviceapi.dto.response.LanguageResponse;
>>>>>>> feature/problem-and-submission-service
import com.technokratos.problemserviceapi.dto.response.TagResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;

import java.util.List;

public interface BaseMetadataService {
<<<<<<< HEAD
   List<Difficulty> getDifficulties();

    List<TagResponse> getTags();
=======
    List<Difficulty> getDifficulties();

    List<TagResponse> getTags();

    List<LanguageResponse> getLanguage();
>>>>>>> feature/problem-and-submission-service
}
