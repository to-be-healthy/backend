package com.tobe.healthy;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class MessageTest {

	@Test
	void generateUUID() {
	    // given
		String number = UUID.randomUUID().toString();
		int result = (int) (Math.random() * 899999) + 100000;
		log.info("UUID = {}", result);
	}
}
