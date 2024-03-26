package com.tobe.healthy.config;

import lombok.Data;

@Data
public class OAuthServiceProperties {
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String redirectUri; // Google과 Kakao에만 존재
	private String tokenUri;
	private String userInfoUri;
}
