package com.technokratos.problemserviceimpl.mapper;

import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
import com.technokratos.problemserviceimpl.entity.Wrapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WrapperMapper {
    WrapperResponse toResponse(Wrapper wrapper);

    List<WrapperResponse> toResponse(List<Wrapper> wrapper);
}
