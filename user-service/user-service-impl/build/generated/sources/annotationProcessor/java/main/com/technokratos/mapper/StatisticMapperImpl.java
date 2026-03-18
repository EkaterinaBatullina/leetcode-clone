package com.technokratos.mapper;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.model.StatisticEntity;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-31T20:57:47+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class StatisticMapperImpl implements StatisticMapper {

    @Override
    public StatisticResponse toResponse(StatisticEntity statisticEntity) {
        if ( statisticEntity == null ) {
            return null;
        }

        UUID userId = null;
        int solvedTasks = 0;
        int attempts = 0;
        int easy = 0;
        int medium = 0;
        int hard = 0;
        int successPercentage = 0;

        userId = statisticEntity.getUserId();
        solvedTasks = statisticEntity.getSolvedTasks();
        attempts = statisticEntity.getAttempts();
        easy = statisticEntity.getEasy();
        medium = statisticEntity.getMedium();
        hard = statisticEntity.getHard();
        successPercentage = statisticEntity.getSuccessPercentage();

        StatisticResponse statisticResponse = new StatisticResponse( userId, solvedTasks, attempts, easy, medium, hard, successPercentage );

        return statisticResponse;
    }
}
