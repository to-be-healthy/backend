package com.tobe.healthy.member.application

import com.tobe.healthy.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository
) {

    @Test
    fun `모든 회원을 조회한다`() {
        // given
        val findMembers = memberRepository.findAll()
        println(findMembers)
    }
}