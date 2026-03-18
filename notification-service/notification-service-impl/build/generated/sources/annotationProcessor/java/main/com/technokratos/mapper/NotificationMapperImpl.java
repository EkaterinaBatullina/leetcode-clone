package com.technokratos.mapper;

import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.model.Notification;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-31T14:37:41+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        String username = null;
        String email = null;
        Status status = null;
        Instant createdAt = null;

        username = notification.getUsername();
        email = notification.getEmail();
        status = notification.getStatus();
        createdAt = notification.getCreatedAt();

        NotificationResponse notificationResponse = new NotificationResponse( username, email, status, createdAt );

        return notificationResponse;
    }
}
