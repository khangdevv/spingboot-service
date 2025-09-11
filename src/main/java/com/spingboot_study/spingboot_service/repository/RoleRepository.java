package com.spingboot_study.spingboot_service.repository;

import com.spingboot_study.spingboot_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {

}
