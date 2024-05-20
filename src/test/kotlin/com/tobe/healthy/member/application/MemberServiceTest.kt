package com.tobe.healthy.member.application

import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_ID_DUPLICATION
import com.tobe.healthy.config.error.ErrorCode.MEMBER_ID_NOT_VALID
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.dto.`in`.CommandLoginMember
import com.tobe.healthy.member.domain.dto.`in`.FindMemberId
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest(
    private val memberAuthService: MemberAuthService,
    private val memberAuthCommandService: MemberAuthCommandService,
    private val memberCommandService: MemberCommandService,
    private val memberRepository: MemberRepository,
    private val redisService: RedisService,
) : StringSpec({

    lateinit var student: Member

    beforeTest {
        student = memberRepository.findByUserId("healthy-student0").get()
    }

    "아이디가 중복됐을경우 예외를 발생시킨다" {
        shouldThrow<CustomException> {
            memberAuthService.validateUserIdDuplication("healthy-student0")
        }.message shouldBe MEMBER_ID_DUPLICATION.message
    }

    "아이디가 4자 미만일 경우 예외를 발생시킨다" {
        shouldThrow<CustomException> {
            memberAuthService.validateUserIdDuplication("abc")
        }.message shouldBe MEMBER_ID_NOT_VALID.message
    }

    "유효한 이메일일 경우 인증번호를 전송한다" {
        val email = memberAuthCommandService.sendEmailVerification("laborlawseon@gmail.com")
        redisService.getValues(email).length shouldBe 6
        email shouldBe "laborlawseon@gmail.com"
    }

    "올바른 로그인 정보를 입력하면 로그인에 성공한다" {
        val token = memberAuthCommandService.login(
            CommandLoginMember(
                student.userId,
                "12345678a",
                STUDENT
            )
        )

        token.userId shouldBe "healthy-student0"
        token.memberType shouldBe STUDENT
        token.accessToken.isNotBlank() shouldBe true
        token.refreshToken.isNotBlank() shouldBe true

    }

    "토큰을 갱신한다" {
        val token = memberAuthCommandService.login(
            CommandLoginMember(
                student.userId,
                "12345678a",
                STUDENT
            )
        )
        val refreshToken = memberAuthCommandService.refreshToken(student.userId, token.refreshToken)

        token.userId shouldBe refreshToken.userId
        token.accessToken shouldNotBe refreshToken.accessToken
        token.refreshToken shouldBe refreshToken.refreshToken
    }

    "프로필 사진을 등록한다" {
        val response = memberCommandService.registerProfile(MockMultipartFile("file", "file1.txt", "text/plain", "some content".toByteArray()), student.id)
        response.fileName shouldStartWith "profile/"
        response.fileUrl shouldStartWith "https://"
    }

    "프로필 사진을 삭제한다" {
        memberCommandService.registerProfile(MockMultipartFile("file", "file1.txt", "text/plain", "some content".toByteArray()), student.id)
        val response = memberCommandService.deleteProfile(student.id)
        response.fileName shouldStartWith "profile/"
        response.fileUrl shouldStartWith "https://"
    }

    "프로필 사진이 없을 때 삭제를 시도하면 예외가 발생한다" {
        val exception = shouldThrow<IllegalArgumentException> {
            memberCommandService.deleteProfile(student.id)
        }

        exception.message shouldBe "프로필 사진이 없습니다."
    }

    "회원이 닉네임을 변경한다" {
        val changeName = memberCommandService.changeName("미미미누", student.id)
        log.info { "변경된 닉네임: ${changeName}"}
        changeName shouldBe "미미미누"
    }

    "회원이 아이디를 찾는다" {
        val request = FindMemberId(
            "healthy-trainer0@gmail.com",
            "healthy-trainer",
            TRAINER
        )
        val response = memberAuthService.findUserId(request)
        log.info { "response: ${response}" }
    }
})
