package com.technokratos.mapper;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.model.StatisticEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    StatisticResponse toResponse(StatisticEntity statisticEntity);
}