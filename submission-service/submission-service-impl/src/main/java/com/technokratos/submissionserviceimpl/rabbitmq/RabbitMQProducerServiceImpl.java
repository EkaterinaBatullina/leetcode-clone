package com.technokratos.submissionserviceimpl.rabbitmq;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;
import com.technokratos.submissionserviceimpl.service.ProblemTestcasesService;
import com.technokratos.submissionserviceimpl.service.SubmissionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RabbitMQProducerServiceImpl implements RabbitMQProducerService {
    private final RabbitTemplate rabbitTemplate;
    private final ProblemTestcasesService problemTestcasesService;
    private final SubmissionService submissionService;

    @Override
    public void sendUserUpdateRequest(SubmissionRequest request) {
        try {
            Difficulty difficulty = problemTestcasesService.getProblemDifficulty(request.problemId());
            boolean isFirstSuccessfulAttempt = submissionService.isFirstSuccessfulAttempt(request.userId(), request.problemId());
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                    request.userId(),
                    difficulty,
                    request.status(),
                    isFirstSuccessfulAttempt
            );
            log.debug("user update request: {}", userUpdateRequest);
            rabbitTemplate.convertAndSend("submission-exchange", "submission-routing-key", userUpdateRequest);
        } catch (Exception e) {
            log.error("error while sending user update request", e);
        }
    }
}
