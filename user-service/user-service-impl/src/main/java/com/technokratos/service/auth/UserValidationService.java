package com.technokratos.service.auth;

import com.technokratos.dto.enums.Role;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.model.UserEntity;
import com.technokratos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.technokratos.util.SecurityConstant.PROFILE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity validateUser(AuthenticationRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return user;
    }

    public UserEntity extractUserFromToken(Jwt refreshToken) {
        UUID profileId = UUID.fromString(refreshToken.getClaim(PROFILE_ID));
        return userRepository.findById(profileId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserEntity getOrCreateUserFromGoogle(String email, String username) {
        log.info("Attempting to find user by email: {}", email);
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            log.info("Found existing user with email {}: {}", email, existingUser.get().getUuid());
            return existingUser.get();
        }

        log.info("No existing user found. Creating new user with email: {}", email);
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole(Role.USER);

        UUID userId = userRepository.save(newUser);
        newUser.setUuid(userId);
        log.info("Created new user with UUID: {}", userId);

        return newUser;
    }
}