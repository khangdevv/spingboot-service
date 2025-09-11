package com.spingboot_study.spingboot_service.service;

import com.spingboot_study.spingboot_service.dto.request.UserCreationRequest;
import com.spingboot_study.spingboot_service.dto.request.UserUpdationRequest;
import com.spingboot_study.spingboot_service.dto.response.UserResponse;
import com.spingboot_study.spingboot_service.entity.User;
import com.spingboot_study.spingboot_service.enums.Role;
import com.spingboot_study.spingboot_service.exception.AppException;
import com.spingboot_study.spingboot_service.exception.ErrorCode;
import com.spingboot_study.spingboot_service.mapper.UserMapper;
import com.spingboot_study.spingboot_service.repository.RoleRepository;
import com.spingboot_study.spingboot_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Use final for fields to ensure immutability
@Slf4j
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper; // Assuming you have a UserMapper for mapping requests to User entity

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

//        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
//                .id(request.getId())
//                .username(request.getUsername())
//                .password(request.getPassword())
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .dob(request.getDob())
//                .build();
        // tac dung cua builder la de tao mot doi tuong moi ma khong can phai khoi tao lai tat ca cac thuoc tinh

        User user = userMapper.toUser(request); // Convert request to User entity using mapper
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt the password

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name()); // Default role for new users

        //user.setRoles(roles); // Set default roles

        return userMapper.toUserResponse(userRepository.save(user));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")// tao ra mot proxy de kiem tra quyen truoc khi thuc hien phuong thuc
    public List<UserResponse> findAllUsers() {
        log.info("In method findAllUsers of UserService");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse) // Convert each User entity to UserResponse using mapper
                .toList();
    }

    @PostAuthorize("returnObject.username == authentication.name") // kiem tra quyen sau khi thuc hien phuong thuc
    public UserResponse findUserById(String id) {
        log.info("In method findUserById of UserService");
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found with id: " + id)));
    }

    public UserResponse updateUser(UserUpdationRequest request, String id) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found with id: " + id));
        userMapper.updateUser(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }
}
