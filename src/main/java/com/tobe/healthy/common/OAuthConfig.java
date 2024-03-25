package com.tobe.healthy.common;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString
public class OAuthConfig {

	@Value("${oauth.kakao.grant-type}")
	private String kakaoGrantType;

	@Value("${oauth.kakao.client-id}")
	private String kakaoClientId;

	@Value("${oauth.kakao.client-secret}")
	private String kakaoClientSecret;

	@Value("${oauth.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Value("${oauth.kakao.token-uri}")
	private String kakaoTokenUri;

	@Value("${oauth.kakao.user-info-uri}")
	private String kakaoUserInfoUri;

	@Value("${oauth.naver.grant-type}")
	private String naverGrantType;

	@Value("${oauth.naver.client-id}")
	private String naverClientId;

	@Value("${oauth.naver.client-secret}")
	private String naverClientSecret;

	@Value("${oauth.naver.token-uri}")
	private String naverTokenUri;

	@Value("${oauth.naver.user-info-uri}")
	private String naverUserInfoUri;

	@Value("${oauth.google.client-id}")
	private String googleClientId;

	@Value("${oauth.google.client-secret}")
	private String googleClientSecret;

	@Value("${oauth.google.token-uri}")
	private String googleTokenUri;

	@Value("${oauth.google.user-info-uri}")
	private String googleUserInfoUri;

	@Value("${oauth.google.grant-type}")
	private String googleGrantType;

	@Value("${oauth.google.redirect-uri}")
	private String googleRedirectUri;

}

