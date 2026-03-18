package com.technokratos.submissionserviceimpl.mapper;

import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.submissionserviceimpl.entity.Testcase;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProblemTestcasesMapper {
    Testcase toEntity(TestcaseResponse testcaseResponse);

    List<Testcase> toEntity(List<TestcaseResponse> testcaseResponses);

    List<TestcaseResponse> toResponse(List<Testcase> testcases);
}
