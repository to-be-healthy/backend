package com.tobe.healthy;

import static com.tobe.healthy.member.domain.entity.Oauth.KAKAO_TOKEN_URL;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class MessageTest {

	@Test
	void generateUUID() {
	    // given
		log.info(KAKAO_TOKEN_URL.getCode());
		log.info(KAKAO_TOKEN_URL.getDescription());
	}
}
