package com.tobe.healthy.config.security;

import static java.util.Base64.getEncoder;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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