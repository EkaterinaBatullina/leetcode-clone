package com.technokratos.repository;

import com.technokratos.entity.Wrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WrapperRepository extends JpaRepository<Wrapper, UUID> {
    Optional<Wrapper> findByProblemIdAndLanguageId(UUID problemId, int languageId);
    List<Wrapper> findAllByProblemId(UUID problemId);
}

