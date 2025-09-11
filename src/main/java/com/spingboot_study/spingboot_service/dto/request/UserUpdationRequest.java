package com.spingboot_study.spingboot_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdationRequest {
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    List<String> roles;
}
