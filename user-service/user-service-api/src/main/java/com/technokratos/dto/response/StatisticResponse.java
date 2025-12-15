package com.technokratos.dto.response;

import java.util.UUID;

public record StatisticResponse(UUID userId, int solvedTasks, int attempts, int easy,
                                int medium, int hard, int successPercentage) {}