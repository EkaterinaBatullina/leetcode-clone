package com.technokratos.model;

import com.technokratos.dto.enums.Status;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {
    private UUID id;
    private String aggregateId;
    private String type;
    private String payload;
    private String topic;
    private Status status;
    private LocalDateTime createdAt;
}
