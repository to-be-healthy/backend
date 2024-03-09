package com.tobe.healthy.config.error;

import static com.tobe.healthy.config.error.ErrorCode.HANDLE_ACCESS_DENIED;
import static com.tobe.healthy.config.error.ErrorCode.SERVER_ERROR;
import static com.tobe.healthy.config.error.ErrorResponse.of;

import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException => {}", e.getMessage());
        final ErrorResponse response = of(HANDLE_ACCESS_DENIED);
        return new ResponseEntity<>(response, HANDLE_ACCESS_DENIED.getStatus());
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(final CustomException e) {
        log.error("CustomException => {}", e.getMessage());
        final ErrorResponse response = of(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException => {}", e.getMessage());
        final ErrorResponse response = of(SERVER_ERROR);
        return new ResponseEntity<>(response, SERVER_ERROR.getStatus());
    }
}
