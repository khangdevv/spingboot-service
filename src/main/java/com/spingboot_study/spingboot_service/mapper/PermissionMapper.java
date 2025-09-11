package com.spingboot_study.spingboot_service.mapper;

import com.spingboot_study.spingboot_service.dto.request.PermissionRequest;
import com.spingboot_study.spingboot_service.dto.response.PermissionResponse;
import com.spingboot_study.spingboot_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
