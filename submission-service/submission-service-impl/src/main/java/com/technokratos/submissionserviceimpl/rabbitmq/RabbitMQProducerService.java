package com.technokratos.submissionserviceimpl.rabbitmq;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;

public interface RabbitMQProducerService {
    void sendUserUpdateRequest(SubmissionRequest request);
}
