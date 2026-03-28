package com.technokratos.handler;

import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.enums.Action;
import com.technokratos.rabbitmq.RabbitMQProducerServiceImpl;
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
