package com.spingboot_study.spingboot_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(
        JsonInclude.Include.NON_NULL) // This annotation ensures that null fields are not included in the JSON response
public class ApiResponse<T> {
    int code;
    String message;
    T data;
}
