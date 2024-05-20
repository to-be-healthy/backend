package com.tobe.healthy.push.repository

import com.tobe.healthy.push.domain.entity.MemberToken
import org.springframework.data.jpa.repository.JpaRepository

interface MemberTokenRepository : JpaRepository<MemberToken, Long> {
    fun findByMemberId(memberId: Long): MemberToken?
}