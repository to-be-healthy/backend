package com.tobe.healthy.member.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class BearerToken {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "bearer_token_id")
    private Long id;
    private Long memberId;
    private String email;
    private String refreshToken;
    private String accessToken;

    public BearerToken(Long memberId, String email, String refreshToken, String accessToken) {
        this.memberId = memberId;
        this.email = email;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public void exchangeAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
