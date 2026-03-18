package com.technokratos.problemserviceimpl.repository;

import com.technokratos.problemserviceimpl.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>  {
}
