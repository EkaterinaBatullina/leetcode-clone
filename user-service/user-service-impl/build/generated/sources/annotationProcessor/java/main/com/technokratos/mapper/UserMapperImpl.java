package com.technokratos.mapper;

import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.UserResponse;
import com.technokratos.model.UserEntity;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-31T20:57:47+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(UserFullRequest userFullRequest) {
        if ( userFullRequest == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.username( userFullRequest.username() );
        userEntity.email( userFullRequest.email() );
        userEntity.password( userFullRequest.password() );

        return userEntity.build();
    }

    @Override
    public UserResponse toResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UUID uuid = null;
        String username = null;
        String email = null;
        String role = null;

        uuid = userEntity.getUuid();
        username = userEntity.getUsername();
        email = userEntity.getEmail();
        if ( userEntity.getRole() != null ) {
            role = userEntity.getRole().name();
        }

        UserResponse userResponse = new UserResponse( uuid, username, email, role );

        return userResponse;
    }
}
