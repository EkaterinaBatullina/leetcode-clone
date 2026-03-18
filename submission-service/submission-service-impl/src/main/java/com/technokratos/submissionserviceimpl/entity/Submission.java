package com.technokratos.submissionserviceimpl.entity;

import com.technokratos.submissionserviceapi.dto.response.Judge0Response;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "submission")
public class Submission {
    @Id
    private String id;
    @Field("user_id")
    private UUID userId;
    @Field("problem_id")
    private UUID problemId;
    @Field("language_id")
    private int languageId;
    @Field("source_code")
    private String sourceCode;
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    private List<Judge0Response> responses;
}
