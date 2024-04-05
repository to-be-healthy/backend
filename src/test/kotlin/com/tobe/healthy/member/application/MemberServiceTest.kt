package com.tobe.healthy.member.application

import com.querydsl.core.QueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.member.domain.entity.QMember.member
import com.tobe.healthy.member.repository.MemberRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val queryFactory: JPAQueryFactory
) {

    @Test
    fun `모든 회원을 조회한다`() {
        // given
        val findMembers = memberRepository.findAll()
        println(findMembers)
    }

    @Test
    fun `QueryDSL로 모든 회원을 조회한다`() {
        val members = queryFactory
                .select(member)
                .from(member)
                .fetch()
        for (member in members) {
            println(member)
        }
    }
}