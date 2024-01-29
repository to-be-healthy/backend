package com.tobe.healthy.config.error;

import static com.tobe.healthy.config.error.ErrorCode.HANDLE_ACCESS_DENIED;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.SERVER_ERROR;
import static com.tobe.healthy.config.error.ErrorResponse.of;
import static org.springframework.http.HttpStatusCode.valueOf;

import com.tobe.healthy.config.error.exception.CustomIllegalArgumentException;
import com.tobe.healthy.config.error.exception.MemberDuplicateException;
import com.tobe.healthy.config.error.exception.MemberNotFoundException;
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
        log.error("handleAccessDeniedException", e);
        final ErrorResponse response = of(HANDLE_ACCESS_DENIED);
        return new ResponseEntity<>(response, valueOf(HANDLE_ACCESS_DENIED.getStatus()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleException(final MemberNotFoundException e) {
        log.error("memberNotFoundException: {}", e.getMessage());
        final ErrorResponse response = of(MEMBER_NOT_FOUND);
        return new ResponseEntity<>(response, valueOf(MEMBER_NOT_FOUND.getStatus()));
    }

    @ExceptionHandler(MemberDuplicateException.class)
    protected ResponseEntity<ErrorResponse> handleException(final MemberDuplicateException e) {
        log.error("memberDuplicateException: {}", e.getMessage());
        final ErrorResponse response = of(MEMBER_DUPLICATION);
        return new ResponseEntity<>(response, valueOf(MEMBER_DUPLICATION.getStatus()));
    }

    @ExceptionHandler(CustomIllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleException(final CustomIllegalArgumentException e) {
        log.error("handleException: {}", e.getMessage());
        final ErrorResponse response = of(e.getMessage());
        return new ResponseEntity<>(response, valueOf(SERVER_ERROR.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException: {}", e.getMessage());
        final ErrorResponse response = of(SERVER_ERROR);
        return new ResponseEntity<>(response, valueOf(SERVER_ERROR.getStatus()));
    }
}
