package com.technokratos.service;

import com.technokratos.dto.enums.Role;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.dto.response.UserResponse;
import com.technokratos.exception.type.UserNotFoundException;
import com.technokratos.mapper.UserMapper;
import com.technokratos.model.UserEntity;
import com.technokratos.repository.UserRepository;
import com.technokratos.service.auth.AuthenticationService;
import com.technokratos.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    StatisticService statisticService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper mapper;

    @Test
    void getById() {
        String username = "username";
        String email = "email";
        UUID uuid = UUID.randomUUID();
        UserEntity entity = new UserEntity(uuid, username, email, "password", Role.USER);
        UserResponse expectedResponse = new UserResponse(uuid, username, email, Role.USER.name());

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(userRepository.findById(uuid)).thenReturn(Optional.of(entity));

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            UserResponse actualResponse = userService.getById();
            assertEquals(expectedResponse, actualResponse);

            verify(mapper).toResponse(entity);
            verify(userRepository).findById(uuid);
        }
    }

    @Test
    void getById_userNotFound() {
        UUID uuid = UUID.randomUUID();
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            when(userRepository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getById());

            verify(userRepository).findById(uuid);
            verifyNoInteractions(mapper);
        }
    }

    @Test
    void getByUsername() {
        UUID uuid = UUID.randomUUID();
        String username = "username";
        String email = "email@gmail.com";
        UserEntity entity = new UserEntity(uuid, username, email, "password", Role.USER);
        UserResponse expectedResponse = new UserResponse(uuid, username, email, Role.USER.name());

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        UserResponse actualResponse = userService.getByUsername(username);

        assertEquals(actualResponse, expectedResponse);
        verify(mapper).toResponse(entity);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getByUsername_userNotFound() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByUsername(username));

        verify(userRepository).findByUsername(username);
        verifyNoInteractions(mapper);
    }

    @Test
    void getAll() {
        UUID uuid = UUID.randomUUID();
        String username = "username";
        String email = "email";
        UserEntity entity = new UserEntity(uuid, username, email, "password", Role.USER);
        UserResponse response = new UserResponse(uuid, username, email, Role.USER.name());
        List<UserEntity> entities = List.of(entity);

        Pageable pageable = Mockito.mock(Pageable.class);
        PageImpl<UserEntity> expectedPage = new PageImpl<>(entities, pageable, entities.size());

        when(userRepository.findAll(pageable)).thenReturn(expectedPage);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<UserResponse> actualPage = userService.getAll(pageable);

        assertEquals(1, actualPage.getTotalElements());
        assertEquals(1, actualPage.getContent().size());
        assertEquals(response, actualPage.getContent().get(0));
        verify(mapper).toResponse(entity);
        verify(userRepository).findAll(pageable);

    }

    @Test
    void delete() {
        UUID uuid = UUID.randomUUID();
        doNothing().when(userRepository).deleteById(uuid);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            userService.delete();

            verify(userRepository).deleteById(uuid);
        }
    }

    @Test
    void create() {
        String expectedUsername = "username";
        String expectedEmail = "email@gmail.com";
        String expectedPassword = "password";
        UUID expectedUuid = UUID.randomUUID();
        String expectedAccessToken = "access-token";
        String expectedRefreshToken = "refresh-token";
        UserEntity userEntity =
                new UserEntity(expectedUuid, expectedUsername, expectedEmail, "encodedPassword", Role.USER);

        when(mapper.toEntity(any(UserFullRequest.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(expectedUuid);
        when(authenticationService.signIn(any(AuthenticationRequest.class)))
                .thenReturn(new TokenCoupleResponse(expectedAccessToken, expectedRefreshToken));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(statisticService).create(expectedUuid);

        TokenCoupleResponse response = userService.create(
                new UserFullRequest(expectedUsername, expectedEmail, expectedPassword)
        );

        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode(expectedPassword);
        verify(statisticService).create(expectedUuid);
        verify(authenticationService).signIn(
                argThat(authRequest -> authRequest.username().equals(expectedUsername) &&
                        authRequest.password().equals(expectedPassword))
        );
        verify(mapper).toEntity(any(UserFullRequest.class));
    }

    @Test
    void update() {
        UUID uuid = UUID.randomUUID();
        String username = "username";
        String email = "email";
        String password = "password";
        UserEntity entity = new UserEntity(uuid, username, email, password, Role.USER);
        UserFullRequest request = new UserFullRequest(username, email, password);

        when(userRepository.findById(uuid)).thenReturn(Optional.of(entity));
        doNothing().when(userRepository).update(any(UserEntity.class));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            userService.update(request);
        }

        verify(userRepository).findById(uuid);
        verify(userRepository).update(any(UserEntity.class));
        verify(passwordEncoder).encode(password);
    }

    @Test
    void update_userNotFound() {
        UUID uuid = UUID.randomUUID();
        UserFullRequest request = new UserFullRequest("user", "email", "pass");

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            when(userRepository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.update(request));

            verify(userRepository).findById(uuid);
            verify(userRepository, never()).update(any());
        }
    }

    @Test
    void patch() {
        UUID uuid = UUID.randomUUID();
        String username = "username";
        String password = "password";
        UserEntity entity = new UserEntity(uuid, username, null, password, Role.USER);
        UserPartialRequest request = new UserPartialRequest(username, null, password);

        when(userRepository.findById(uuid)).thenReturn(Optional.of(entity));
        doNothing().when(userRepository).update(any(UserEntity.class));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            userService.patch(request);
        }

        verify(userRepository).findById(uuid);
        verify(passwordEncoder).encode(password);
        verify(userRepository).update(any(UserEntity.class));
    }

    @Test
    void patch_userNotFound() {
        UUID uuid = UUID.randomUUID();
        UserPartialRequest request = new UserPartialRequest("user", "email", "pass");

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            when(userRepository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.patch(request));

            verify(userRepository).findById(uuid);
            verify(userRepository, never()).update(any());
        }
    }

    @Test
    void updateRole() {
        UUID uuid = UUID.randomUUID();
        RoleRequest request = new RoleRequest(Role.ADMIN.name());
        UserEntity entity = new UserEntity(uuid, "username", "email", "password", Role.USER);

        when(userRepository.findById(uuid)).thenReturn(Optional.of(entity));
        doNothing().when(userRepository).updateRole(any(UserEntity.class));

        userService.updateRole(uuid, request);

        verify(userRepository).findById(uuid);
        verify(userRepository).updateRole(any(UserEntity.class));
    }

    @Test
    void updateRole_userNotFound() {
        UUID uuid = UUID.randomUUID();
        RoleRequest request = new RoleRequest(Role.ADMIN.name());

        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateRole(uuid, request));

        verify(userRepository).findById(uuid);
        verify(userRepository, never()).updateRole(any());
    }
}