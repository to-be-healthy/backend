package com.tobe.healthy.kotest.study

import com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@Transactional
class KotestStudy2 @Autowired constructor(
    @PersistenceContext
    private val em: EntityManager
) : BehaviorSpec({
    fun extensions() = listOf(SpringExtension) // extension 활성화


    given("사용자가 회원가입을 하고") {
        val member = Member.builder()
            .userId("trainer")
            .email("trainer@hotmail.com")
            .password("123456789a")
            .name("정선우")
            .memberType(TRAINER)
            .pushAlarmStatus(ENABLED)
            .feedbackAlarmStatus(ENABLED)
            .build()
        `when`("로그인을 하면") {
            em.persist(member)
            then("토큰을 반환한다") {
                member.name shouldBe "정선우"
            }
        }
    }
})
