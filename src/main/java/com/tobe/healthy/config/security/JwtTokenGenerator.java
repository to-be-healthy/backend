package com.tobe.healthy.config.security;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
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

        String role = "ROLE_" + member.getMemberType().name();

        String accessToken = createAccessToken(member.getId(), member.getUserId(), role, getAccessTokenValid(nowInMilliseconds));

        String refreshToken = createRefreshToken(member.getId(), member.getUserId(), getRefreshTokenValid(nowInMilliseconds));

        redisService.setValuesWithTimeout(member.getUserId(), refreshToken, getRefreshTokenValid(nowInMilliseconds).getTime());

        return new Tokens(accessToken, refreshToken, member.getUserId());
    }

    private Date getRefreshTokenValid(long nowInMilliseconds) {
        return new Date(nowInMilliseconds + refreshTokenValidSeconds * 1000);
    }

    private Date getAccessTokenValid(long nowInMilliseconds) {
        return new Date(nowInMilliseconds + accessTokenValidSeconds * 1000);
    }

    public Tokens exchangeAccessToken(Long memberId, String userId, MemberType memberType, String refreshToken) {
        long nowInMilliseconds = new Date().getTime();
        String role = "ROLE_" + memberType.name();
        String changedAccessToken = createAccessToken(memberId, userId, role, getAccessTokenValid(nowInMilliseconds));
        return new Tokens(changedAccessToken, refreshToken, userId);
    }

    private String createAccessToken(Long memberId, String userId, String role, Date expiry) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("uuid", UUID.randomUUID().toString());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key, HS256)
                .compact();
    }

    private String createRefreshToken(Long memberId, String userId, Date expiry) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("userId", userId);
        claims.put("uuid", UUID.randomUUID().toString());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key, HS256)
                .compact();
    }
}