package com.tobe.healthy.config.security;

import static java.lang.String.valueOf;

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
        String accessToken = createAccessToken(
                valueOf(member.getId()),
                member.getEmail(),
                "ROLE_MEMBER",
                new Date(nowInMilliseconds + accessTokenValidSeconds * 1000));

        String refreshToken = createRefreshToken(new Date(nowInMilliseconds + refreshTokenValidSeconds * 1000));

        redisService.setValuesWithTimeout(member.getEmail(), refreshToken, new Date(nowInMilliseconds + refreshTokenValidSeconds * 1000).getTime());

        return new Tokens(accessToken, refreshToken);
    }

    public Tokens exchangeAccessToken(Member member, String refreshToken) {
        long nowInMilliseconds = new Date().getTime();
        String changedAccessToken = createAccessToken(
                valueOf(member.getId()),
                member.getEmail(),
                "ROLE_MEMBER",
                new Date(nowInMilliseconds + accessTokenValidSeconds * 1000));
        return new Tokens(changedAccessToken, refreshToken);
    }

    private String createAccessToken(String userId, String email, String role, Date expiry) {
        Claims claims = Jwts.claims().setSubject(userId).setExpiration(expiry).setIssuedAt(new Date());
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
