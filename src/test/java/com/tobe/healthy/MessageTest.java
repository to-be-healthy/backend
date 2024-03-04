package com.tobe.healthy;

import com.tobe.healthy.common.RedisService;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class MessageTest {

	@Autowired
	private RedisService redisService;

	@Test
	void generateAuthCode() {
		String authKey = getAuthCode();
		redisService.setValuesWithTimeout("laborlawseon@gmail.com", authKey, 3 * 60 * 1000);
	}

	@Test
	void getRandomStr() {
	    // given
		String randomStr = RandomStringUtils.random(12, true, true);
		log.info("randomStr => {}", randomStr);
	}

	private String getAuthCode() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		int num = 0;

		while (buffer.length() < 6) {
			num = random.nextInt(10);
			buffer.append(num);
		}

		return buffer.toString();
	}
}
