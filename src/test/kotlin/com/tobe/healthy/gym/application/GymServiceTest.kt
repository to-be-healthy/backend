package com.tobe.healthy.gym.application

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.application.MemberService
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.member.repository.MemberRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotlinGymServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val queryFactory: JPAQueryFactory,
    private val memberService: MemberService,
    private val gymService: GymService,
    private val passwordEncoder: PasswordEncoder,
    private val em: EntityManager,
    private val gymRepository: GymRepository
) {

    @BeforeEach
    fun `각 테스트마다 임시 회원을 생성한다`() {
        val student = Member.builder()
            .userId("test1234")
            .email("test1234@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("test1234")
            .memberType(STUDENT)
            .build()
        memberRepository.save(student)

        val trainer = Member.builder()
            .userId("test12345")
            .email("test12345@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("test12345")
            .memberType(TRAINER)
            .build()
        memberRepository.save(trainer)
    }

    @Test
    fun `학생이 내 헬스장으로 등록한다`() {
        val findMember = memberRepository.findByUserId("test1234").orElseThrow()
        gymService.selectMyGym(10L, 0, findMember.id)
        em.flush()
        em.clear()
        assertThat(findMember.gym.id).isEqualTo(10)
    }

    @Test
    fun `트레이너가 인증코드 없이 내 헬스장으로 등록해 실패한다`() {
        val findMember = memberRepository.findByUserId("test12345").orElseThrow()

        val message = assertThrows<CustomException> {
            gymService.selectMyGym(10L, 0, findMember.id)
        }.message

        assertThat(message).isEqualTo(JOIN_CODE_NOT_VALID.message)
    }

    @Test
    fun `관리자 또는 트레이너가 헬스장을 등록한다`() {
        gymService.registerGym("선우짐 서초점")
        val findGym = gymRepository.findByName("선우짐 서초점").orElseThrow()
        assertThat(findGym.name).isEqualTo("선우짐 서초점")
    }

    @Test
    fun `헬스장에 속한 트레이너들을 조회한다`() {
        val lists = gymService.findAllTrainersByGym(1)
        for (list in lists) {
            log.info { "list => ${list}" }
        }
    }
}