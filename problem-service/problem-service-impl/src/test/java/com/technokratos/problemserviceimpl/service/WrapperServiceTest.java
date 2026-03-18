package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
import com.technokratos.problemserviceimpl.BaseServiceTest;
import com.technokratos.problemserviceimpl.entity.Wrapper;
import com.technokratos.problemserviceimpl.mapper.WrapperMapper;
import com.technokratos.problemserviceimpl.repository.WrapperRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class WrapperServiceTest extends BaseServiceTest {

    @Autowired
    private WrapperService wrapperService;

    @MockitoBean
    private WrapperRepository wrapperRepository;

    @MockitoBean
    private WrapperMapper wrapperMapper;

    @Test
    void findByProblemIdAndLanguageId() {
        UUID problemId = UUID.randomUUID();
        int languageId = 1;
        Wrapper wrapper = new Wrapper();
        WrapperResponse response = new WrapperResponse("signature", "wrapper");

        when(wrapperRepository.findByProblemIdAndLanguageId(problemId, languageId))
                .thenReturn(Optional.of(wrapper));
        when(wrapperMapper.toResponse(wrapper)).thenReturn(response);

        Optional<WrapperResponse> result =
                wrapperService.findByProblemIdAndLanguageId(problemId, languageId);

        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(wrapperRepository).findByProblemIdAndLanguageId(problemId, languageId);
    }

    @Test
    void findByProblemIdAndLanguageId_notFound() {
        UUID problemId = UUID.randomUUID();
        int languageId = 2;

        when(wrapperRepository.findByProblemIdAndLanguageId(problemId, languageId))
                .thenReturn(Optional.empty());

        Optional<WrapperResponse> result =
                wrapperService.findByProblemIdAndLanguageId(problemId, languageId);

        assertFalse(result.isPresent());
        verify(wrapperRepository).findByProblemIdAndLanguageId(problemId, languageId);
    }
}