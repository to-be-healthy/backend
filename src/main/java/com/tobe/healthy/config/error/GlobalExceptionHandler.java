package com.tobe.healthy.config.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.config.error.ErrorResponse.of;
import static org.springframework.http.HttpStatusCode.valueOf;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException => {}", e.getMessage());
        final ErrorResponse response = of(HANDLE_ACCESS_DENIED);
        return new ResponseEntity<>(response, valueOf(HANDLE_ACCESS_DENIED.getStatus()));
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleException(final CustomException e) {
        log.error("CustomException => {}", e.getMessage());
        final ErrorResponse response = of(e.getErrorCode());
        return new ResponseEntity<>(response, valueOf(e.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException: {}", e.getMessage());
        final ErrorResponse response = of(SERVER_ERROR);
        return new ResponseEntity<>(response, valueOf(SERVER_ERROR.getStatus()));
    }
}
