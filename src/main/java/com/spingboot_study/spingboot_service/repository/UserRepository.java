package com.spingboot_study.spingboot_service.repository;

import java.util.Optional;

import com.spingboot_study.spingboot_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username); // Check if a user with the given username already exists

    Optional<User> findByUsername(String username);
}
