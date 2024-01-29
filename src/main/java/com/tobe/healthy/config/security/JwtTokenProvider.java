package com.tobe.healthy.config.security;

import static java.lang.String.valueOf;

import com.tobe.healthy.member.application.CustomMemberDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider { // JWT 토큰 검증 모듈

    private final CustomMemberDetailService customMemberDetailService;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Request의 Header에서 token파싱
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        log.info("header = {}", header);
        return request.getHeader("Authorization");
    }

    public Claims decode(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        Claims claims = decode(token.substring("Bearer ".length()));
        UserDetails userDetails = customMemberDetailService.loadUserByUsername(valueOf(claims.get("email")));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken.substring("Bearer ".length()));
        return !claims.getBody().getExpiration().before(new Date());// 만료시간이 현재시간보다 전인지 확인
    }
}
