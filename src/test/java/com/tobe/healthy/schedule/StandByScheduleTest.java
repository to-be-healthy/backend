package com.tobe.healthy.schedule;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class StandByScheduleTest {
	@Autowired
	private EntityManager em;

	@Test
	void 예약_대기를_한다() {
	    // given

	}
}