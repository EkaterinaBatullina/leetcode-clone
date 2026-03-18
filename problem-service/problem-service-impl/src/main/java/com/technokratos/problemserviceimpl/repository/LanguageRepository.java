package com.technokratos.problemserviceimpl.repository;

import com.technokratos.problemserviceimpl.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {
}
