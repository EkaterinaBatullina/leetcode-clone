package com.technokratos.repository;

import com.technokratos.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<UserEntity> findById(UUID uuid);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findAll(Pageable pageable);

    UUID save(UserEntity userEntity);

    void update(UserEntity userEntity);

    void updateRole(UserEntity userEntity);

    void deleteById(UUID uuid);

    void deleteByUsername(String username);
}