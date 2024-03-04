package com.tobe.healthy.config.security;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {
    private final Long accessTokenValidSeconds;
    private final Long refreshTokenValidSeconds;
    private final String jwtSecret;
    private final Key key;
    private final RedisService redisService;

    public JwtTokenGenerator(@Value("${jwt.access-token-valid-seconds}") Long accessTokenValidSeconds,
                             @Value("${jwt.refresh-token-valid-seconds}") Long refreshTokenValidSeconds,
                             @Value("${jwt.secret}")  String jwtSecret,
                             RedisService redisService) {
        this.accessTokenValidSeconds = accessTokenValidSeconds;
        this.refreshTokenValidSeconds = refreshTokenValidSeconds;
        this.jwtSecret = jwtSecret;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.redisService = redisService;
    }

    public Tokens create(Member member) {
        long nowInMilliseconds = new Date().getTime();
        String accessToken = createAccessToken(member.getEmail(), "ROLE_MEMBER", getAccessTokenValid(nowInMilliseconds));

        String refreshToken = createRefreshToken(getRefreshTokenValid(nowInMilliseconds));

        redisService.setValuesWithTimeout(member.getEmail(), refreshToken, getRefreshTokenValid(nowInMilliseconds).getTime());

        return new Tokens(accessToken, refreshToken);
    }

    private Date getRefreshTokenValid(long nowInMilliseconds) {
        return new Date(nowInMilliseconds + refreshTokenValidSeconds * 1000);
    }

    private Date getAccessTokenValid(long nowInMilliseconds) {
        return new Date(nowInMilliseconds + accessTokenValidSeconds * 1000);
    }

    public Tokens exchangeAccessToken(Member member, String refreshToken) {
        long nowInMilliseconds = new Date().getTime();
        String changedAccessToken = createAccessToken(member.getEmail(), "ROLE_MEMBER", getAccessTokenValid(nowInMilliseconds));
        return new Tokens(changedAccessToken, refreshToken);
    }

    private String createAccessToken(String email, String role, Date expiry) {
        Claims claims = Jwts.claims().setExpiration(expiry).setIssuedAt(new Date());
        claims.put("email", email);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    private String createRefreshToken(Date expiry) {
        return Jwts.builder()
                .setClaims(Jwts.claims().setExpiration(expiry).setIssuedAt(new Date()))
                .signWith(key)
                .compact();
    }
}