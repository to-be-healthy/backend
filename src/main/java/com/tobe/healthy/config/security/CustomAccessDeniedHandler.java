package com.tobe.healthy.config.security;

import static com.tobe.healthy.config.error.ErrorCode.HANDLE_ACCESS_DENIED;
import static com.tobe.healthy.config.error.ErrorResponse.of;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.config.error.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponse exceptionResponse = of(HANDLE_ACCESS_DENIED);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.error("CustomAccessDeniedHandler => {}", accessDeniedException.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HANDLE_ACCESS_DENIED.getStatus().value());

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, exceptionResponse);
            os.flush();
        }
    }
}
