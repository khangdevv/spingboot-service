package com.spingboot_study.spingboot_service.mapper;

import com.spingboot_study.spingboot_service.dto.request.UserCreationRequest;
import com.spingboot_study.spingboot_service.dto.request.UserUpdationRequest;
import com.spingboot_study.spingboot_service.dto.response.UserResponse;
import com.spingboot_study.spingboot_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    // @Mapping(target = "id", ignore = false) // Ignore id field during mapping
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true) // Ignore roles field during mapping
    void updateUser(
            UserUpdationRequest request,
            @MappingTarget
                    User user); // Method to update an existing User entity with new data from UserCreationRequest
}
