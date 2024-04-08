package com.tobe.healthy

import com.tobe.healthy.member.repository.MemberRepository
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

val log = KotlinLogging.logger {}


@SpringBootTest
class KotlinHealthyApplicationTests @Autowired constructor(
    val memberRepository: MemberRepository
) {

    @Test
    fun findMember() {
        val members = memberRepository.findAll()
        for (member in members) {
            println(member)
        }
    }
}