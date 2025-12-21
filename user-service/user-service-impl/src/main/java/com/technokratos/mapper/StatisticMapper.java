package com.technokratos.mapper;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.model.StatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "solvedTasks", target = "solvedTasks")
    @Mapping(source = "attempts", target = "attempts")
    @Mapping(source = "easy", target = "easy")
    @Mapping(source = "medium", target = "medium")
    @Mapping(source = "hard", target = "hard")
    @Mapping(source = "successPercentage", target = "successPercentage")
    StatisticResponse toResponse(StatisticEntity statisticEntity);
}