package com.technokratos.model;

import com.technokratos.dto.enams.Status;
import com.technokratos.event.UserRegisteredEvent;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;
    @Indexed
    private UUID userId;
    private String email;
    @Indexed
    private Status status;
    private Instant createdAt;
    private UserRegisteredEvent eventPayload;
    private String errorType;
    private String errorMessage;
}
