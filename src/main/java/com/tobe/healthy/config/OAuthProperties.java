package com.tobe.healthy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("oauth")
@Data
public class OAuthProperties {
	private OAuthServiceProperties kakao;
	private OAuthServiceProperties naver;
	private OAuthServiceProperties google;
}
