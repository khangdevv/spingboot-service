package com.spingboot_study.spingboot_service.mapper;

import com.spingboot_study.spingboot_service.dto.request.RoleCreationRequest;
import com.spingboot_study.spingboot_service.dto.response.RoleResponse;
import com.spingboot_study.spingboot_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    RoleResponse toRoleResponse(Role role);
}
