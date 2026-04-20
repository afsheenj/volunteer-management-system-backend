package com.vserve.project.exception;


import com.vserve.project.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<?> handleResourceNotFoundException(Exception e){
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(Exception e){
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolation(ConstraintViolationException e){
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ApiResponse.error(errorMessage);
    }

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<?> handleValidation(ValidationException e){
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGenericExceptions(Exception e){
        return ApiResponse.error(e.getMessage());
    }
}
