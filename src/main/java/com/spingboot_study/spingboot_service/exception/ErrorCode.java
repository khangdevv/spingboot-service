package com.spingboot_study.spingboot_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_KEY(1000, "Invalid key provided", HttpStatus.BAD_REQUEST),
    USER_EXISTS(1001, "User already exists with this username", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1008, "User does not exist", HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(9999, "An uncategorized error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1002, "Username must be at least 3 characters long and can only contain alphanumeric characters and underscores", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters long", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "User is not authenticated", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1006, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1007, "Token is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1010, "User is not authorized to perform this action", HttpStatus.FORBIDDEN),
    DOB_INVALID(1011, "Your age must be at least {min}", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
