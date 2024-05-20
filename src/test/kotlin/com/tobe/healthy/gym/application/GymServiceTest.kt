package com.tobe.healthy.gym.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class GymServiceTest(
    private val gymCommandService: GymCommandService,
    private val gymRepository: GymRepository,
    private val memberRepository: MemberRepository,
) : StringSpec({

    "헬스장을 등록한다" {
        // given
        val name = "건강해짐 화정점"

        // when
        val response = gymCommandService.registerGym(name)

        // then
        response.name shouldBe name
        response.joinCode.length shouldBe 6
    }

    "내 헬스장으로 등록한다" {
        val findGym = gymRepository.findByName("건강해짐 원흥점") ?: throw IllegalArgumentException("헬스장을 찾을 수 없습니다.")
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val response = gymCommandService.selectMyGym(findGym.id, findGym.joinCode, trainer.id)
        response.id shouldBe findGym.id
        response.name shouldBe "건강해짐 원흥점"
    }

    "내 헬스장으로 등록할 때 가입 번호가 틀리면 예외가 발생한다" {
        val findGym = gymRepository.findByName("건강해짐 원흥점")
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val message = shouldThrow<CustomException> {
            gymCommandService.selectMyGym(findGym!!.id, "12345", trainer.id)
        }.message

        message shouldBe JOIN_CODE_NOT_VALID.message
    }
})
