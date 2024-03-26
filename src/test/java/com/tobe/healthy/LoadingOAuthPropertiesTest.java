package com.tobe.healthy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class LoadingOAuthPropertiesTest {

	@Autowired
	private com.tobe.healthy.config.OAuthProperties OAuthProperties;

	@Test
	void loadingProperties() {
	    log.info("kakao => {}", OAuthProperties.getKakao());
	    log.info("naver => {}", OAuthProperties.getNaver());
	    log.info("google => {}", OAuthProperties.getGoogle());
	}
}