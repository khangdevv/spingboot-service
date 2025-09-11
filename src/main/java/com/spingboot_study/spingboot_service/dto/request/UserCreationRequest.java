package com.spingboot_study.spingboot_service.dto.request;

import com.spingboot_study.spingboot_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // Use FieldDefaults to set the default access level for fields
public class UserCreationRequest {
    private String id;

    @Size(min=3, message = "USERNAME_INVALID")
    String username;

    @Size(min=8, message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "DOB_INVALID")
    LocalDate dob;
}
