package com.technokratos.rabbitmq;

import com.technokratos.dto.request.SubmissionRequest;

public interface RabbitMQProducerService {
    void sendUserUpdateRequest(SubmissionRequest request);
}
