package com.tobe.healthy;

import com.tobe.healthy.common.OAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class HealthyApplicationTests {

	@Autowired
	private OAuthConfig oAuthConfig;

	@Test
	void contextLoads() {
		log.info("oAuthConfig => {}", oAuthConfig);
	}
}
