package com.spingboot_study.spingboot_service.controller;

import com.spingboot_study.spingboot_service.dto.request.ApiResponse;
import com.spingboot_study.spingboot_service.dto.request.PermissionRequest;
import com.spingboot_study.spingboot_service.dto.request.RoleCreationRequest;
import com.spingboot_study.spingboot_service.dto.response.PermissionResponse;
import com.spingboot_study.spingboot_service.dto.response.RoleResponse;
import com.spingboot_study.spingboot_service.service.PermissionService;
import com.spingboot_study.spingboot_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles") // base URL for user-related endpoints
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createPermission(@RequestBody RoleCreationRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAllPermissions() {
        return ApiResponse.<List<RoleResponse>>builder()
                .data(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<Void> deletePermission(@PathVariable String name) {
        roleService.delete(name);
        return ApiResponse.<Void>builder()
                .message("Permission deleted successfully")
                .build();
    }
}
