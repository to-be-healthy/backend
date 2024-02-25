package com.tobe.healthy.config.security;

import static java.util.Base64.getEncoder;

import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class JwtTokenGeneratorTest {

	@Test
	void generateTokenKey() {
	    // given
		SecureRandom random = new SecureRandom();
		byte[] key = new byte[32];
		random.nextBytes(key);
		log.info("key = {}", key);
		String jwtKey = getEncoder().encodeToString(key);
		// when
		log.info("encoded jwtKey = {}", jwtKey);
	}
}