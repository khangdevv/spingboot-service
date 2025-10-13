package com.spingboot_study.spingboot_service.service;

import java.util.List;

import com.spingboot_study.spingboot_service.dto.request.PermissionRequest;
import com.spingboot_study.spingboot_service.dto.response.PermissionResponse;
import com.spingboot_study.spingboot_service.entity.Permission;
import com.spingboot_study.spingboot_service.mapper.PermissionMapper;
import com.spingboot_study.spingboot_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String name) {
        permissionRepository.deleteById(name);
    }
}
