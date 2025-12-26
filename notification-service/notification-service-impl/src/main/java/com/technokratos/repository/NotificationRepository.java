package com.technokratos.repository;

import com.technokratos.model.Notification;
import com.technokratos.dto.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Page<Notification> findByStatus(Status status, Pageable pageable);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);
}
