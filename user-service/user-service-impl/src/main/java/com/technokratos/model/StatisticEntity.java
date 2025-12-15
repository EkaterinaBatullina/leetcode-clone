package com.technokratos.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticEntity {
    private UUID userId;
    private int solvedTasks;
    private int attempts;
    private int easy;
    private int medium;
    private int hard;
    private int successPercentage;

    @Override
    public String toString() {
        return "StatisticEntity {userId='%s'}".formatted(userId);
    }
}

/*
{
        "userId": "e3f9f8e4-ec3e-4a57-b1c4-51b8a1cd8c8f",
        "difficulty": "medium",
        "status": "success", или "failure"
        "isFirstSuccessfulAttempt": true
        }
*/