package com.tobe.healthy.gym.application

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.gym.domain.entity.Gym
import com.tobe.healthy.logger
import com.tobe.healthy.member.application.MemberService
import com.tobe.healthy.member.domain.dto.`in`.MemberJoinCommand
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.repository.MemberRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotlinGymServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val queryFactory: JPAQueryFactory,
    private val memberService: MemberService,
    private val gymService: GymService,
    private val passwordEncoder: PasswordEncoder,
    private val em: EntityManager
) {

//    @BeforeEach
//    fun `각 테스트마다 임시 회원 생성`(@Autowired memberService: MemberService) {
//        val entity = Member.builder()
//            .userId("test1234")
//            .email("test1234@gmail.com")
//            .password(passwordEncoder.encode("zxcvbnm11"))
//            .name("test1234")
//            .memberType(STUDENT)
//            .build()
//        memberRepository.save(entity)
//    }

    @Test
    fun `학생이 내 헬스장으로 등록한다`() {
        val entity = Member.builder()
            .userId("test1234")
            .email("test1234@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("test1234")
            .memberType(STUDENT)
            .build()
        memberRepository.save(entity)
        val findMember = memberRepository.findByUserId("test1234").orElseThrow()
        val gym = Gym.builder().name("합정점").build()
        findMember.registerGym(gym)
//        gymService.selectMyGym(7, 0, findMember.id)
    em.flush()
    em.clear()
    }
}