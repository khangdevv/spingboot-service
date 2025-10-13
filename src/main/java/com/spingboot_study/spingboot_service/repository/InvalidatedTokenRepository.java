package com.spingboot_study.spingboot_service.repository;

import com.spingboot_study.spingboot_service.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
