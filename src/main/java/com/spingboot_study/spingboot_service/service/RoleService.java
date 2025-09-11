package com.spingboot_study.spingboot_service.service;

import com.spingboot_study.spingboot_service.dto.request.RoleCreationRequest;
import com.spingboot_study.spingboot_service.dto.response.RoleResponse;
import com.spingboot_study.spingboot_service.entity.Role;
import com.spingboot_study.spingboot_service.mapper.RoleMapper;
import com.spingboot_study.spingboot_service.repository.PermissionRepository;
import com.spingboot_study.spingboot_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final PermissionRepository permissionRepository;

    public RoleResponse create(RoleCreationRequest request){
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll(){
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String name) {
        roleRepository.deleteById(name);
    }

}
