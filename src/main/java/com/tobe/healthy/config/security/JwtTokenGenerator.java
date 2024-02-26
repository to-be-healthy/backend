package com.tobe.healthy.config.security;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.BearerToken;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.BearerTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.tobe.healthy.config.error.ErrorCode.ACCESS_TOKEN_NOT_FOUND;
import static java.lang.String.valueOf;

@Component
public class JwtTokenGenerator {
    private final Long accessTokenValidSeconds;
    private final Long refreshTokenValidSeconds;
    private final String jwtSecret;
    private final Key key;
    private final BearerTokenRepository bearerTokenRepository;

    public JwtTokenGenerator(@Value("${jwt.access-token-valid-seconds}") Long accessTokenValidSeconds,
                             @Value("${jwt.refresh-token-valid-seconds}") Long refreshTokenValidSeconds,
                             @Value("${jwt.secret}")  String jwtSecret,
                             BearerTokenRepository bearerTokenRepository) {
        this.accessTokenValidSeconds = accessTokenValidSeconds;
        this.refreshTokenValidSeconds = refreshTokenValidSeconds;
        this.jwtSecret = jwtSecret;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.bearerTokenRepository = bearerTokenRepository;
    }

    public Tokens create(Member member) {
        long nowInMilliseconds = new Date().getTime();
        String accessToken = createAccessToken(
                valueOf(member.getId()),
                member.getEmail(),
                "ROLE_MEMBER",
                new Date(nowInMilliseconds + accessTokenValidSeconds * 1000));
        String refreshToken = createRefreshToken(new Date(nowInMilliseconds + refreshTokenValidSeconds * 1000));
        bearerTokenRepository.deleteAllByMemberId(member.getId());
        bearerTokenRepository.save(new BearerToken(member.getId(), member.getEmail(), refreshToken, accessToken));
        return new Tokens(accessToken, refreshToken);
    }

    public Tokens exchangeAccessToken(Member member, String accessToken) {
        BearerToken token = bearerTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new CustomException(ACCESS_TOKEN_NOT_FOUND));

        long nowInMilliseconds = new Date().getTime();
        String changedAccessToken = createAccessToken(
                valueOf(member.getId()),
                member.getEmail(),
                "ROLE_MEMBER",
                new Date(nowInMilliseconds + accessTokenValidSeconds * 1000));
        token.exchangeAccessToken(changedAccessToken);
        bearerTokenRepository.save(token);
        return new Tokens(changedAccessToken, token.getRefreshToken());
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
