package com.tobe.healthy.common;

import com.tobe.healthy.config.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final JwtTokenProvider jwtTokenProvider;

    public Long getRequesterId(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String accessToken = authHeader.substring(7);
        log.info("accessToken: {}", accessToken);
        String memberId = jwtTokenProvider.getUsernameFromToken(accessToken);
        return Long.parseLong(memberId);
    }

}
