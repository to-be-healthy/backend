package com.tobe.healthy.member.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tokens {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String role;
}
