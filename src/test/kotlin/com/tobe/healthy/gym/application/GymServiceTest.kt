package com.tobe.healthy.gym.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class GymServiceTest(
    private val gymService: GymService,
    private val memberRepository: MemberRepository
) : StringSpec({

    var gymId = 0L
    var joinCode = 0

    beforeTest {
        // given
        val name = "건강해짐 화정점"

        // when
        val response = gymService.registerGym(name)
        gymId = response.id
        joinCode = response.joinCode
    }

    "헬스장을 등록한다" {
        // given
        val name = "건강해짐 원흥점"

        // when
        val response = gymService.registerGym(name)

        // then
        response.name shouldBe name
        response.joinCode.toString().length shouldBe 6
    }

    "내 헬스장으로 등록한다" {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val response = gymService.selectMyGym(gymId, joinCode, trainer.id)
        response.id shouldBe gymId
        response.name shouldBe "건강해짐 화정점"
    }

    "내 헬스장으로 등록할 때 가입 번호가 틀려 예외가 발생한다" {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val message = shouldThrow<CustomException> {
            gymService.selectMyGym(gymId, 12345, trainer.id)
        }.message

        message shouldBe JOIN_CODE_NOT_VALID.message
    }
})
