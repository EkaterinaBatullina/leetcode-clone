package com.technokratos.submissionserviceimpl.handler;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.enums.Action;
import com.technokratos.submissionserviceimpl.rabbitmq.RabbitMQProducerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RabbitMQUserUpdateRequestHandler {
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;

    public void handle(Action action, SubmissionRequest request) {
        if (Objects.requireNonNull(action) == Action.SUBMIT) {
            rabbitMQProducerService.sendUserUpdateRequest(request);
        } else {
            throw new IllegalArgumentException("unknown action: " + action);
        }
    }
}
