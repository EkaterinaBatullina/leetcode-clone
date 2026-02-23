package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.dto.enums.Role;
import com.technokratos.dto.enums.Status;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.mapper.UserMapper;
import com.technokratos.model.OutboxEntity;
import com.technokratos.model.UserEntity;
import com.technokratos.repository.OutboxRepository;
import com.technokratos.repository.UserRepository;
import com.technokratos.service.auth.AuthenticationService;
import com.technokratos.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import com.technokratos.dto.response.UserResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final StatisticService statisticService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final KafkaProducerProperties properties;

    @Override
    @Cacheable(value = "users", key = "T(com.technokratos.util.SecurityUtil).getCurrentUserId()")
    public UserResponse getById() {
        UUID uuid = SecurityUtil.getCurrentUserId();
        return userMapper.toResponse(
                userRepository.findById(uuid)
                        .orElseThrow(() -> new UserNotFoundException(uuid))
        );
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserResponse getByUsername(String username) {
        return userMapper.toResponse(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username))
        );
    }

    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public TokenCoupleResponse create(UserFullRequest userFullRequest) {
        UserEntity userEntity = userMapper.toEntity(userFullRequest);
        String rawPassword = userFullRequest.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userEntity.setPassword(encodedPassword);
        userEntity.setRole(Role.USER);
        UUID uuid = userRepository.save(userEntity);
        userEntity.setUuid(uuid);
        statisticService.create(uuid);

        UserRegisteredEvent event = new UserRegisteredEvent(UUID.randomUUID(), uuid, userFullRequest.username(),  userFullRequest.email());

        try {
            outboxRepository.save(OutboxEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(uuid.toString())
                    .type("USER_REGISTERED")
                    .payload(objectMapper.writeValueAsString(event))
                    .topic(properties.getUserRegisteredTopic())
                    .status(Status.NEW)
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for user: {}", uuid, e);
        }

        return authenticationService.signIn(
                new AuthenticationRequest(userEntity.getUsername(), rawPassword)
        );
    }

    @Override
    @CacheEvict(value = "users", key = "T(com.technokratos.util.SecurityUtil).getCurrentUserId()")
    public void delete() {
        UUID uuid = SecurityUtil.getCurrentUserId();
        userRepository.deleteById(uuid);
    }

    @Override
    @CacheEvict(value = "users", key = "T(com.technokratos.util.SecurityUtil).getCurrentUserId()")
    public void update(UserFullRequest userFullRequest) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        UserEntity userEntity = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        userEntity = userEntity.toBuilder()
                .username(userFullRequest.username())
                .email(userFullRequest.email())
                .password(passwordEncoder.encode(userFullRequest.password()))
                .build();
        userRepository.update(userEntity);
    }

    @Override
    @CacheEvict(value = "users", key = "T(com.technokratos.util.SecurityUtil).getCurrentUserId()")
    public void patch(UserPartialRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        UserEntity userEntity = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        userEntity = userEntity.toBuilder()
                .username(request.username() != null ? request.username() : userEntity.getUsername())
                .email(request.email() != null ? request.email() : userEntity.getEmail())
                .password(request.password() != null ? passwordEncoder.encode(request.password()) : userEntity.getPassword())
                .build();
        userRepository.update(userEntity);
    }

    @Override
    @CacheEvict(value = "users", key = "#uuid")
    public void updateRole(UUID uuid, RoleRequest roleRequest) {
        UserEntity userEntity = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        userEntity.setRole(Role.valueOf(roleRequest.role()));
        userRepository.updateRole(userEntity);
    }
}