package com.tobe.healthy.member.application

import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_ID_DUPLICATION
import com.tobe.healthy.config.error.ErrorCode.MEMBER_ID_NOT_VALID
import com.tobe.healthy.member.domain.dto.`in`.MemberLoginCommand
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val redisService: RedisService,
) : StringSpec({

    lateinit var student: Member

    beforeTest {
        student = memberRepository.findByUserId("healthy-student0").get()
    }

    "아이디가 중복됐을경우 예외를 발생시킨다" {
        shouldThrow<CustomException> {
            memberService.validateUserIdDuplication("healthy-student0")
        }.message shouldBe MEMBER_ID_DUPLICATION.message
    }

    "아이디가 4자 미만일 경우 예외를 발생시킨다" {
        shouldThrow<CustomException> {
            memberService.validateUserIdDuplication("abc")
        }.message shouldBe MEMBER_ID_NOT_VALID.message
    }

    "유효한 이메일일 경우 인증번호를 전송한다" {
        val email = memberService.sendEmailVerification("laborlawseon@gmail.com")
        redisService.getValues(email).length shouldBe 6
        email shouldBe "laborlawseon@gmail.com"
    }

    "올바른 로그인 정보를 입력하면 로그인에 성공한다" {
        val token = memberService.login(MemberLoginCommand(student.userId, "12345678a", STUDENT))

        token.userId shouldBe "healthy-student0"
        token.memberType shouldBe STUDENT
        token.accessToken.isNotBlank() shouldBe true
        token.refreshToken.isNotBlank() shouldBe true
    }

    "토큰을 갱신한다" {
        val token = memberService.login(MemberLoginCommand(student.userId, "12345678a", STUDENT))
        val refreshToken = memberService.refreshToken(student.userId, token.refreshToken)

        token.userId shouldBe refreshToken.userId
        token.accessToken shouldNotBe refreshToken.accessToken
        token.refreshToken shouldBe refreshToken.refreshToken
    }
})
