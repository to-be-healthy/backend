package com.tobe.healthy.kotest.study

import com.tobe.healthy.log
import com.tobe.healthy.member.application.MemberService
import com.tobe.healthy.member.domain.dto.`in`.MemberFindIdCommand
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotestStudy2 (private val memberService: MemberService) : BehaviorSpec({
    extensions(SpringExtension)

    given("ddd") {
        log.info { "given" }
        val request = MemberFindIdCommand("laborlawseon@gmail.com", "정선우", TRAINER)
        val findMember = memberService.findUserId(request)
        `when`("ddd") {
            log.info { findMember }
            log.info { "when" }
            then("ddd") {
                val findMember = memberService.findUserId(request)
                log.info { "findMember ${findMember}" }
            }
        }
    }
})
