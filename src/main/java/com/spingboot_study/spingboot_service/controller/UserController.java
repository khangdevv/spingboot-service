package com.spingboot_study.spingboot_service.controller;

import java.util.List;

import com.spingboot_study.spingboot_service.dto.request.ApiResponse;
import com.spingboot_study.spingboot_service.dto.request.UserCreationRequest;
import com.spingboot_study.spingboot_service.dto.request.UserUpdationRequest;
import com.spingboot_study.spingboot_service.dto.response.UserResponse;
import com.spingboot_study.spingboot_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users") // base URL for user-related endpoints
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(
            @RequestBody @Valid UserCreationRequest request) { // mapping request body to User entity
        ApiResponse<UserResponse> response = new ApiResponse<>();
        UserResponse user = userService.createUser(request);
        response.setCode(201); // HTTP status code for created
        response.setMessage("User created successfully");
        response.setData(user);
        return response;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> findAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.findAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> findUserById(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.findUserById(id))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder().data(userService.getMyInfo()).build();
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@RequestBody UserUpdationRequest request, @PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    void deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
    }
}
