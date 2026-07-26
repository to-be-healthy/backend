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
	private AppleProperties apple;

	/**
	 * 애플은 client_secret을 매 요청마다 ES256으로 서명해 생성하기 때문에
	 * 다른 소셜과 설정 항목이 다르다.
	 */
	@Data
	@ToString
	public static class AppleProperties {
		private String teamId;
		private String clientId;
		private String keyPath;
		private String loginKey;
		private String redirectUri;
	}

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

		public String getAdminKey() {
			return adminKey;
		}
	}
}
