package com.tobe.healthy.common;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;

import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
class RedisServiceTest {

	@Autowired
	private RedisService redisService;

	@Autowired
	private JwtTokenGenerator tokenGenerator;

	@Autowired
	private EntityManager em;

	@Test
	void registerTest() {
	    // given
	    redisService.setValues("redis", "Hello Redis!");

		String value = redisService.getValues("redis");
		log.info("value => {}", value);
	}

	@Test
	void registerToken() {
		Member member = Member.builder()
				.email("laborlawseon@gmail.com")
				.password("12345678")
				.nickname("seonwoo_jung")
				.isAlarm(ABLE)
				.category(MEMBER)
				.build();

		em.persist(member);

		tokenGenerator.create(member);
		String value = redisService.getValues(member.getEmail());
		log.info("value => {}", value);
	}
}