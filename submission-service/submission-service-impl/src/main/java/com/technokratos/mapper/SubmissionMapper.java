package com.technokratos.mapper;

import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.dto.response.SubmissionResponse;
import com.technokratos.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubmissionMapper {
    SubmissionResponse toResponse(Submission submission);

    Submission toEntity(SubmissionRequest submissionRequest);

    List<SubmissionResponse> toResponse(List<Submission> submissions);

    List<Submission> toEntity(List<SubmissionRequest> submissionRequests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responses", source = "responses")
    void updateEntity(SubmissionRequest submissionRequest, @MappingTarget Submission submission);
}