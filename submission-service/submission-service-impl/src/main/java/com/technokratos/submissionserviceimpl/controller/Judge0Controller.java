package com.technokratos.submissionserviceimpl.controller;

<<<<<<< HEAD
import com.technokratos.submissionserviceapi.api.external.Judge0Api;
=======
import com.technokratos.submissionserviceapi.api.internal.Judge0Api;
>>>>>>> feature/problem-and-submission-service
import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.Judge0Response;
import com.technokratos.submissionserviceapi.dto.response.RunResponse;
import com.technokratos.submissionserviceapi.enums.Action;
import com.technokratos.submissionserviceimpl.assembler.SubmissionAssembler;
<<<<<<< HEAD
import com.technokratos.submissionserviceimpl.handler.RabbitMQUserUpdateRequestHandler;
import com.technokratos.submissionserviceimpl.handler.SubmissionActionHandler;
import com.technokratos.submissionserviceimpl.redis.RedisCleanupService;
import com.technokratos.submissionserviceimpl.redis.RedisMetadataService;
import com.technokratos.submissionserviceimpl.redis.RedisService;
import com.technokratos.submissionserviceimpl.service.SubmissionService;
import io.micrometer.core.instrument.MeterRegistry;
=======
import com.technokratos.submissionserviceimpl.handler.SubmissionActionHandler;
import com.technokratos.submissionserviceimpl.redis.RedisCleanupService;
import com.technokratos.submissionserviceimpl.redis.RedisService;
import com.technokratos.submissionserviceimpl.service.SubmissionService;
>>>>>>> feature/problem-and-submission-service
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class Judge0Controller implements Judge0Api {
    private final RedisCleanupService redisCleanupService;
    private final RedisService redisService;
<<<<<<< HEAD
    private final RedisMetadataService redisMetadataService;
    private final SubmissionAssembler submissionAssembler;
    private final SubmissionActionHandler submissionActionHandler;
    private final SubmissionService submissionService;
    private final RabbitMQUserUpdateRequestHandler updateRequestHandler;
=======
    private final SubmissionAssembler submissionAssembler;
    private final SubmissionActionHandler submissionActionHandler;
    private final SubmissionService submissionService;

>>>>>>> feature/problem-and-submission-service

    @Override
    public ResponseEntity<Void> handleCallback(Judge0Response response) {
        log.debug("judge0response: {}", response);
        if (response == null) {
            log.error("received null Judge0Response");
            return ResponseEntity.badRequest().build();
        }
        String token = response.token();
<<<<<<< HEAD
        String submissionId = redisMetadataService.getSubmissionIdFromToken(token);
=======
        String submissionId = redisService.getSubmissionIdFromToken(token);
>>>>>>> feature/problem-and-submission-service
        if (submissionId == null) {
            log.warn("unknown token: {}", token);
            return ResponseEntity.ok().build();
        }
<<<<<<< HEAD
        redisService.storeResponse(submissionId, response);
        if (!redisService.allResponsesReceived(submissionId)) {
            return ResponseEntity.ok().build();
        }
        List<Judge0Response> allResponses = redisService.getAllResponses(submissionId);
        RunResponse runResponse = new RunResponse(UUID.randomUUID(), allResponses);
        log.debug("run response: {}", runResponse);
        SubmissionRequest request = submissionAssembler.build(submissionId, allResponses);
        submissionService.update(submissionId, request);
        String actionRaw = Optional.ofNullable(
                redisMetadataService.getAction(submissionId)
=======
        boolean isSingleRequest = redisService.isSingleRequest(submissionId);
        redisService.storeResponse(submissionId, response);
        if (isSingleRequest) {
            SubmissionRequest request = submissionAssembler.build(submissionId, List.of(response));
            submissionService.update(submissionId, request);
            RunResponse runResponse = new RunResponse(UUID.fromString(submissionId), request.status(), List.of(response));
            log.debug("run response: {}", runResponse);
            String actionRaw = Optional.ofNullable(
                    redisService.getAction(submissionId)
            ).orElse(Action.RUN.name());
            try {
                Action action = Action.valueOf(actionRaw.trim().toUpperCase());
                submissionActionHandler.handle(action, runResponse);
            } catch (IllegalArgumentException e) {
                log.error("unknown action '{}' for submission {}", actionRaw, submissionId, e);
            }
            redisCleanupService.clearTokenMappingsBySubmissionId(submissionId);
            redisCleanupService.clearSubmission(submissionId);
            return ResponseEntity.ok().build();
        }
        if (!redisService.allResponsesReceived(submissionId) && testAccepted(response.status().id())) {
            return ResponseEntity.ok().build();
        }
        List<Judge0Response> allResponses = redisService.getAllResponses(submissionId);
        SubmissionRequest request = submissionAssembler.build(submissionId, allResponses);
        submissionService.update(submissionId, request);
        RunResponse runResponse = new RunResponse(UUID.fromString(submissionId), request.status(), allResponses);
        log.debug("run response: {}", runResponse);
        String actionRaw = Optional.ofNullable(
                redisService.getAction(submissionId)
>>>>>>> feature/problem-and-submission-service
        ).orElse(Action.RUN.name());
        try {
            Action action = Action.valueOf(actionRaw.trim().toUpperCase());
            submissionActionHandler.handle(action, runResponse);
<<<<<<< HEAD
            // updateRequestHandler.handle(action, request);
=======
>>>>>>> feature/problem-and-submission-service
        } catch (IllegalArgumentException e) {
            log.error("unknown action '{}' for submission {}", actionRaw, submissionId, e);
        }
        redisCleanupService.clearTokenMappingsBySubmissionId(submissionId);
        redisCleanupService.clearSubmission(submissionId);
        return ResponseEntity.ok().build();
    }
<<<<<<< HEAD
=======

    private boolean testAccepted(int status) {
        return status == 3;
    }
>>>>>>> feature/problem-and-submission-service
}