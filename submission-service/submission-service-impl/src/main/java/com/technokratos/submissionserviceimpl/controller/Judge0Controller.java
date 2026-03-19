package com.technokratos.submissionserviceimpl.controller;

import com.technokratos.submissionserviceapi.api.internal.Judge0Api;
import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.Judge0Response;
import com.technokratos.submissionserviceapi.dto.response.RunResponse;
import com.technokratos.submissionserviceapi.enums.Action;
import com.technokratos.submissionserviceimpl.assembler.SubmissionAssembler;
import com.technokratos.submissionserviceimpl.handler.RabbitMQUserUpdateRequestHandler;
import com.technokratos.submissionserviceimpl.handler.SubmissionActionHandler;
import com.technokratos.submissionserviceimpl.redis.RedisCleanupService;
import com.technokratos.submissionserviceimpl.redis.RedisService;
import com.technokratos.submissionserviceimpl.service.SubmissionService;
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
    private final SubmissionAssembler submissionAssembler;
    private final SubmissionActionHandler submissionActionHandler;
    private final SubmissionService submissionService;
    private final RabbitMQUserUpdateRequestHandler rabbitHandler;


    @Override
    public ResponseEntity<Void> handleCallback(Judge0Response response) {
        log.debug("judge0response: {}", response);
        if (response == null) {
            log.error("received null Judge0Response");
            return ResponseEntity.badRequest().build();
        }
        String token = response.token();
        String submissionId = redisService.getSubmissionIdFromToken(token);
        if (submissionId == null) {
            log.warn("unknown token: {}", token);
            return ResponseEntity.ok().build();
        }
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
                rabbitHandler.handle(action, request);
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
        ).orElse(Action.RUN.name());
        try {
            Action action = Action.valueOf(actionRaw.trim().toUpperCase());
            submissionActionHandler.handle(action, runResponse);
            rabbitHandler.handle(action, request);
        } catch (IllegalArgumentException e) {
            log.error("unknown action '{}' for submission {}", actionRaw, submissionId, e);
        }
        redisCleanupService.clearTokenMappingsBySubmissionId(submissionId);
        redisCleanupService.clearSubmission(submissionId);
        return ResponseEntity.ok().build();
    }

    private boolean testAccepted(int status) {
        return status == 3;
    }
}