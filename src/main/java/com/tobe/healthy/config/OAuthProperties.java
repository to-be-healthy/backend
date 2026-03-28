package com.tobe.healthy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Component
@ConfigurationProperties("oauth")
@Data
@ToString
public class OAuthProperties {
	private OAuthServiceProperties kakao;
	private OAuthServiceProperties naver;
	private OAuthServiceProperties google;

	@Data
	@ToString
	public static class OAuthServiceProperties {
		private String grantType;
		private String clientId;
		private String clientSecret;
		private String redirectUri; // Google과 Kakao에만 존재
		private String tokenUri;
		private String userInfoUri;
		private String adminKey;
	}
}
