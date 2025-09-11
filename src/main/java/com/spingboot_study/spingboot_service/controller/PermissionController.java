package com.spingboot_study.spingboot_service.controller;

import com.spingboot_study.spingboot_service.dto.request.ApiResponse;
import com.spingboot_study.spingboot_service.dto.request.PermissionRequest;
import com.spingboot_study.spingboot_service.dto.response.PermissionResponse;
import com.spingboot_study.spingboot_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions") // base URL for user-related endpoints
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .data(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .data(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<Void> deletePermission(@PathVariable String name) {
        permissionService.delete(name);
        return ApiResponse.<Void>builder()
                .message("Permission deleted successfully")
                .build();
    }
}
