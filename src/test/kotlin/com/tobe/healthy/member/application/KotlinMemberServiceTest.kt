package com.tobe.healthy.member.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotlinMemberServiceTest @Autowired constructor(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository
) {

    @Test
    fun `아이디가 중복되면 예외를 반환한다`() {
        val message = assertThrows<CustomException> {
            memberService.validateUserIdDuplication("laborlawseon")
        }.message
        assertThat(message).isEqualTo("사용할 수 없는 아이디입니다.")
    }

    @Test
    fun `아이디가 중복되지 않으면 true를 반환한다`() {
        val result = memberService.validateUserIdDuplication("seonwoo_jjang")
        assertThat(result).isTrue()
    }

    @Test
    fun `지연 로딩을 테스트한다`() {
        // given
        println("===========================================================")
        val result = memberRepository.findById(56L)
        println("===========================================================")
        result.get().profileId
        println("===========================================================")
        result.get().profileId.fileUrl
        println("===========================================================")
        // when

    }
}