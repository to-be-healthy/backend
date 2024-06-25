package com.tobe.healthy.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(final CustomException e) {
        log.error("CustomException => {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(e.getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

	@ExceptionHandler(OAuthException.class)
	protected ResponseEntity<ErrorResponse> handleCustomException(final OAuthException e) {
		log.error("OAuthException => {}", e.getMessage());
		final ErrorResponse response = ErrorResponse.of(e.getMessage());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleException(final IllegalArgumentException e) {
        log.error("IllegalArgumentException => {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(e.getMessage());
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("Exception => {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of("서버에서 에러가 발생하였습니다.");
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        final ErrorResponse response = ErrorResponse.of(errorMessage);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(BAD_REQUEST.value()));
    }
}
