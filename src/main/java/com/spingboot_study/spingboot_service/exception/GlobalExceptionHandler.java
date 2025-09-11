package com.spingboot_study.spingboot_service.exception;

import com.spingboot_study.spingboot_service.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice // This annotation indicates that this class will handle exceptions globally across all controllers
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)// This method will handle all RuntimeExceptions thrown by controllers
    ResponseEntity<ApiResponse<Exception>> handlingRuntimeException(Exception ex) {
        ApiResponse<Exception> response = new ApiResponse<>();
        response.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode()); // HTTP status code for bad request
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = RuntimeException.class)// This method will handle all RuntimeExceptions thrown by controllers
    ResponseEntity<ApiResponse<RuntimeException>> handlingRuntimeException(RuntimeException ex) {
        ApiResponse<RuntimeException> response = new ApiResponse<>();
        response.setCode(400); // HTTP status code for bad request
        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = AppException.class)// This method will handle all RuntimeExceptions thrown by controllers
    ResponseEntity<ApiResponse<AppException>> handlingAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<AppException> response = new ApiResponse<>();
        response.setCode(errorCode.getCode()); // HTTP status code for bad request
        response.setMessage(errorCode.getMessage());
        response.setData(ex);
        return ResponseEntity.status(errorCode.getCode()).body(response);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<AccessDeniedException>> handlingAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.<AccessDeniedException>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)// This method will handle all other exceptions
    ResponseEntity<ApiResponse<MethodArgumentNotValidException>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String enumKey = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // If the enum key is not found, we can log it or handle it accordingly
            errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION; // Fallback to uncategorized error
        }
        ApiResponse<MethodArgumentNotValidException> response = new ApiResponse<>();
        response.setCode(errorCode.getCode()); // HTTP status code for bad request
        response.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
