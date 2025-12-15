package com.technokratos.listener;

import com.rabbitmq.client.Channel;
import com.technokratos.service.StatisticService;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SubmissionEventListener {
    private final StatisticService statisticService;

    @RabbitListener(queues = "submission-queue", containerFactory = "manualAckFactory")
    public void handleSubmissionEvent(UserUpdateRequest request, Channel channel, Message message) throws IOException {
        try {
            statisticService.update(request);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}