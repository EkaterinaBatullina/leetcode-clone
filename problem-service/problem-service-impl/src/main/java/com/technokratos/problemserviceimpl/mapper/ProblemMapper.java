package com.technokratos.problemserviceimpl.mapper;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceimpl.entity.Problem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TestcaseMapper.class, TagMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProblemMapper {
    ProblemResponse toResponse(Problem problem);

    Problem toEntity(ProblemRequest problemRequest);

    List<ProblemResponse> toResponse(List<Problem> problems);

    List<Problem> toEntity(List<ProblemRequest> problemRequests);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ProblemRequest problemRequest, @MappingTarget Problem problem);

}