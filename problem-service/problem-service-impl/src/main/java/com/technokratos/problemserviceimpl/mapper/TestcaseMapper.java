package com.technokratos.problemserviceimpl.mapper;

import com.technokratos.problemserviceapi.dto.request.TestcaseRequest;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceimpl.entity.Testcase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TestcaseMapper {
    TestcaseResponse toResponse(Testcase testcase);

    Testcase toEntity(TestcaseRequest testcaseRequest);

    List<TestcaseResponse> toResponse(List<Testcase> testcases);

    List<Testcase> toEntity(List<TestcaseRequest> testcaseRequests);

    @Mapping(target = "id", ignore = true)
    void updateEntity(TestcaseRequest testcaseRequest, @MappingTarget Testcase testcase);
}
