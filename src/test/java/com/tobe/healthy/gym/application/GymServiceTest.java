package com.tobe.healthy.gym.application;

import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class GymServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EntityManager em;

	@Autowired
	private GymRepository gymRepository;

	@Test
	void registerGym() {
		Member entity = Member.builder()
				.userId("test1234")
				.email("test1234@gmail.com")
				.password(passwordEncoder.encode("zxcvbnm11"))
				.name("test1234")
				.memberType(MemberType.STUDENT)
				.build();


		Member member = memberRepository.save(entity);
		Gym gym = gymRepository.findById(7L).orElseThrow();

		member.registerGym(gym);
		em.flush();
		em.clear();
	}
}
